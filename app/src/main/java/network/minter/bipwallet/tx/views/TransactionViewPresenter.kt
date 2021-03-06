/*
 * Copyright (C) by MinterTeam. 2020
 * @link <a href="https://github.com/MinterTeam">Org Github</a>
 * @link <a href="https://github.com/edwardstock">Maintainer Github</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package network.minter.bipwallet.tx.views

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import network.minter.bipwallet.R
import network.minter.bipwallet.apis.reactive.avatar
import network.minter.bipwallet.databinding.*
import network.minter.bipwallet.internal.helpers.DateHelper.DATE_FORMAT_WITH_TZ
import network.minter.bipwallet.internal.helpers.DateHelper.fmt
import network.minter.bipwallet.internal.helpers.IntentHelper
import network.minter.bipwallet.internal.helpers.MathHelper.humanize
import network.minter.bipwallet.internal.helpers.ResTextFormat
import network.minter.bipwallet.internal.helpers.ViewExtensions.copyOnClick
import network.minter.bipwallet.internal.mvp.MvpBasePresenter
import network.minter.bipwallet.internal.storage.SecretStorage
import network.minter.bipwallet.share.ShareManager
import network.minter.bipwallet.share.SharingText
import network.minter.bipwallet.tx.adapters.TransactionFacade
import network.minter.bipwallet.tx.contract.TransactionView
import network.minter.bipwallet.tx.ui.TransactionViewDialog
import network.minter.core.MinterSDK
import network.minter.explorer.MinterExplorerSDK
import network.minter.explorer.models.HistoryTransaction
import org.joda.time.DateTime
import java.math.BigDecimal
import javax.inject.Inject

/**
 * minter-android-wallet. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class TransactionViewPresenter @Inject constructor() : MvpBasePresenter<TransactionView>() {
    @Inject lateinit var secretStorage: SecretStorage

    private var _tx: TransactionFacade? = null
    private val tx: HistoryTransaction
        get() = _tx!!.tx

    override fun handleExtras(bundle: Bundle?) {
        super.handleExtras(bundle)
        _tx = IntentHelper.getParcelExtra(bundle, TransactionViewDialog.ARG_TX)

        fillData()
    }

    private fun String?.fromBase64(): String? {
        if (this == null || isEmpty()) return null
        return String(Base64.decode(this, 0))
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun fillData() {
        viewState.setOnClickShare(View.OnClickListener {
            startShare()
        })
        viewState.setFromName(_tx?.fromName)
        viewState.setFromAddress(tx.from.toString())
        viewState.setFromAvatar(tx.from.avatar)
        viewState.setToName(null)
        viewState.setToAddress(null)

        viewState.setPayload(tx.payload.fromBase64())
        viewState.setTimestamp(DateTime(tx.timestamp).fmt(DATE_FORMAT_WITH_TZ))
        if (tx.gasCoin.toUpperCase() != MinterSDK.DEFAULT_COIN) {
            viewState.setFee("${tx.gasCoin} (${tx.fee.humanize()} ${MinterSDK.DEFAULT_COIN})")
        } else {
            viewState.setFee("${tx.fee.humanize()} ${tx.gasCoin}")
        }

        viewState.setBlockNumber(tx.block.toString())
        viewState.setBlockClickListener(View.OnClickListener {
            onClickBlockNumber()
        })

        val isIncoming = tx.isIncoming(listOf(secretStorage.mainWallet))

        if (tx.type == HistoryTransaction.Type.Send || tx.type == HistoryTransaction.Type.MultiSend) {
            if (isIncoming) {
                viewState.setTitle(R.string.dialog_title_tx_view_incoming)
            } else {
                viewState.setTitle(R.string.dialog_title_tx_view_outgoing)
            }
        } else {
            when (tx.type) {
                HistoryTransaction.Type.SellCoin,
                HistoryTransaction.Type.SellAllCoins,
                HistoryTransaction.Type.BuyCoin
                -> viewState.setTitleTyped(R.string.tx_type_exchange)
                HistoryTransaction.Type.CreateCoin ->
                    viewState.setTitleTyped(R.string.tx_type_create_coin)
                HistoryTransaction.Type.DeclareCandidacy ->
                    viewState.setTitleTyped(R.string.tx_type_declare_candidacy)
                HistoryTransaction.Type.RedeemCheck ->
                    viewState.setTitleTyped(R.string.tx_type_redeem_check)
                HistoryTransaction.Type.SetCandidateOnline ->
                    viewState.setTitleTyped(R.string.tx_type_set_candidate_online)
                HistoryTransaction.Type.SetCandidateOffline ->
                    viewState.setTitleTyped(R.string.tx_type_set_candidate_offline)
                HistoryTransaction.Type.CreateMultisigAddress ->
                    viewState.setTitleTyped(R.string.tx_type_create_multisig)
                HistoryTransaction.Type.EditCandidate ->
                    viewState.setTitleTyped(R.string.tx_type_edit_candidate)

                else -> viewState.setTitle(ResTextFormat(R.string.fmt_type_of_transaction, tx.type.name))
            }
        }

        when (tx.type) {
            HistoryTransaction.Type.Send -> {
                val data: HistoryTransaction.TxSendCoinResult = tx.getData()
                viewState.setToName(_tx!!.toName)
                viewState.setFromName(_tx!!.fromName)

                viewState.setToAddress(data.to.toString())
                viewState.setToAvatar(data.to.avatar)

                viewState.inflateDetails(R.layout.tx_details_send) {
                    val b = TxDetailsSendBinding.bind(it)
                    b.valueAmount.text = data.amount.humanize()
                    b.valueCoin.text = data.coin
                }
            }
            HistoryTransaction.Type.MultiSend -> {
                if (isIncoming) {
                    val data: HistoryTransaction.TxMultisendResult = tx.getData()
                    val sendItem = data.items.filter { it.to == secretStorage.mainWallet }.toList()

                    if (sendItem.isNotEmpty()) {
                        val entry = sendItem.iterator().next()
                        viewState.setToName(null)
                        viewState.setToAddress(entry.to.toString())
                        viewState.setToAvatar(entry.to.avatar)

                        viewState.inflateDetails(R.layout.tx_details_send) {
                            val b = TxDetailsSendBinding.bind(it)
                            b.valueAmount.text = entry.amount.humanize()
                            b.valueCoin.text = entry.coin
                        }
                    } else {
                        viewState.showTo(false)
                    }
                } else {
                    viewState.showTo(false)
                }
            }
            HistoryTransaction.Type.SellCoin,
            HistoryTransaction.Type.SellAllCoins,
            HistoryTransaction.Type.BuyCoin -> {
                viewState.showTo(false)

                viewState.inflateDetails(R.layout.tx_details_exchange) {
                    val b = TxDetailsExchangeBinding.bind(it)
                    val data: HistoryTransaction.TxConvertCoinResult = tx.getData()
                    b.valueFromCoin.text = data.coinToSell
                    b.valueToCoin.text = data.coinToBuy
                    b.valueAmountReceived.text = data.valueToBuy.humanize()
                    b.valueAmountSpent.text = data.valueToSell.humanize()
                }
            }
            HistoryTransaction.Type.CreateCoin -> {
                viewState.showTo(false)

                viewState.inflateDetails(R.layout.tx_details_create_coin) {
                    val b = TxDetailsCreateCoinBinding.bind(it)
                    val data: HistoryTransaction.TxCreateResult = tx.getData()
                    b.valueCoinName.text = data.name
                    b.valueCoinSymbol.text = data.symbol
                    b.valueInitialAmount.text = data.initialAmount.humanize()
                    b.valueInitialReserve.text = data.initialReserve.humanize()
                    b.valueCrr.text = "${data.constantReserveRatio}%"

                    b.valueMaxSupply.text = if (data.maxSupply < BigDecimal("10").pow(15)) {
                        data.maxSupply.humanize()
                    } else {
                        "10¹⁵ (max)"
                    }
                }
            }
            HistoryTransaction.Type.DeclareCandidacy -> {
                val data: HistoryTransaction.TxDeclareCandidacyResult = tx.getData()

                viewState.setToAvatar(_tx?.toAvatar, R.drawable.img_avatar_candidate)
                viewState.setToName(_tx?.toName)
                viewState.setToAddress(data.publicKey.toString())

                viewState.inflateDetails(R.layout.tx_details_declare_candidacy) {
                    val b = TxDetailsDeclareCandidacyBinding.bind(it)

                    b.valueCommission.text = "${data.commission}%"
                    b.valueCoin.text = data.coin
                    b.valueStake.text = data.stake.humanize()
                }
            }
            HistoryTransaction.Type.Delegate,
            HistoryTransaction.Type.Unbond -> {
                val data: HistoryTransaction.TxDelegateUnbondResult = tx.getData()

                viewState.setToName(_tx?.toName)
                viewState.setToAddress(data.publicKey.toString())

                if (tx.type == HistoryTransaction.Type.Delegate) {
                    viewState.setToAvatar(_tx?.toAvatar, R.drawable.img_avatar_delegate)
                } else {
                    viewState.setToAvatar(_tx?.toAvatar, R.drawable.img_avatar_unbond)
                }

                viewState.inflateDetails(R.layout.tx_details_delegate_unbond) {
                    val b = TxDetailsDelegateUnbondBinding.bind(it)
                    b.valueCoin.text = data.coin
                    b.valueStake.text = data.value.humanize()
                }
            }
            HistoryTransaction.Type.RedeemCheck -> {
                viewState.showTo(true)

                // to = from
                viewState.setToLabel(R.string.label_from)
                viewState.setToName(_tx?.fromName)
                viewState.setToAddress(tx.from.toString())
                viewState.setToAvatar(_tx?.fromAvatar)

                viewState.inflateDetails(R.layout.tx_details_redeem_check) {
                    val b = TxDetailsRedeemCheckBinding.bind(it)
                    val data: HistoryTransaction.TxRedeemCheckResult = tx.getData()
                    val check = data.getCheck()

                    // from = to = issuer
                    viewState.setFromLabel(R.string.label_check_issuer)
                    viewState.setFromAddress(check.sender.toString())
                    viewState.setFromName(_tx?.toName)
                    viewState.setFromAvatar(check.sender.avatar)

                    b.valueAmount.text = check.value.humanize()
                    b.valueCoin.text = check.coin
                }
            }
            HistoryTransaction.Type.SetCandidateOnline,
            HistoryTransaction.Type.SetCandidateOffline -> {
                val data: HistoryTransaction.TxSetCandidateOnlineOfflineResult = tx.getData()

                viewState.setToAvatar(_tx?.toAvatar, R.drawable.img_avatar_candidate)
                viewState.setToAddress(data.publicKey.toString())
                viewState.setToName(_tx?.toName)
            }
            HistoryTransaction.Type.EditCandidate -> {
                val data: HistoryTransaction.TxEditCandidateResult = tx.getData()

                viewState.setToAvatar(_tx?.toAvatar, R.drawable.img_avatar_candidate)
                viewState.setToAddress(data.publicKey.toString())
                viewState.setToName(_tx?.toName)

                viewState.inflateDetails(R.layout.tx_details_edit_candidate) {
                    val b = TxDetailsEditCandidateBinding.bind(it)

                    b.valueOwnerAddress.text = data.ownerAddress.toString()
                    b.valueOwnerAddress.copyOnClick()
                    b.valueRewardAddress.text = data.rewardAddress.toString()
                    b.valueRewardAddress.copyOnClick()
                }
            }
            HistoryTransaction.Type.CreateMultisigAddress -> {
                viewState.showTo(false)

                viewState.inflateDetails(R.layout.tx_details_create_multisig_address) {
                    val data: HistoryTransaction.TxCreateMultisigResult = tx.getData()
                    val b = TxDetailsCreateMultisigAddressBinding.bind(it)
                    b.valueMultisigAddress.text = data.multisigAddress?.toString() ?: "<none>"
                    b.valueMultisigAddress.copyOnClick()
                }

            }
            else -> {
                viewState.showTo(false)
                // nothing to do
            }
        }
    }

    private fun onClickBlockNumber() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                MinterExplorerSDK.newFrontUrl().addPathSegment("transactions").addPathSegment(tx.hash.toString()).build().toString()
        ))
        viewState.startIntent(intent)
    }

    private fun startShare() {
        val text = SharingText()
        text.title = "Transaction " + tx.hash.toShortString()
        text.url = MinterExplorerSDK.newFrontUrl().addPathSegment("transactions").addPathSegment(tx.hash.toString()).build().toString()

        val shareIntent = ShareManager.getInstance().createCommonIntent(text, "Share transaction")
        viewState.startIntent(shareIntent)
    }

}