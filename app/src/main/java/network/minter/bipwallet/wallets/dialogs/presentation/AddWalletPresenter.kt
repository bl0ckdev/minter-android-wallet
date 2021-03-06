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
package network.minter.bipwallet.wallets.dialogs.presentation

import android.view.View
import moxy.InjectViewState
import network.minter.bipwallet.internal.auth.AuthSession
import network.minter.bipwallet.internal.di.annotations.FragmentScope
import network.minter.bipwallet.internal.mvp.MvpBasePresenter
import network.minter.bipwallet.internal.storage.SecretStorage
import network.minter.bipwallet.internal.storage.models.AddressBalanceTotal
import network.minter.bipwallet.wallets.contract.AddWalletView
import network.minter.bipwallet.wallets.selector.WalletItem
import network.minter.profile.models.User
import javax.inject.Inject

@FragmentScope
@InjectViewState
class AddWalletPresenter @Inject constructor() : MvpBasePresenter<AddWalletView>() {
    @Inject lateinit var session: AuthSession
    @Inject lateinit var secretStorage: SecretStorage

    private var mTitle: String? = null
    private var mPhrase: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setOnSubmitClickListener(View.OnClickListener {
            onSubmit()
        })
        viewState.setOnGenerateClickListener(View.OnClickListener {
            viewState.startGenerate()
        })
        viewState.addFormValidListener {
            viewState.setEnableSubmit(it)
        }

        viewState.addTextChangedListener { input, valid ->
            when (input.fieldName) {
                "title" -> {
                    if (valid) {
                        mTitle = input.text?.trim().toString()
                    }
                }
                "seed" -> {
                    if (valid) {
                        mPhrase = input.text.toString()
                    }
                }
            }
        }
    }

    private fun onSubmit() {
        val address = secretStorage.add(mPhrase!!, mTitle)
        secretStorage.setMain(address)
        session.login(
                AuthSession.AUTH_TOKEN_ADVANCED,
                User(AuthSession.AUTH_TOKEN_ADVANCED),
                AuthSession.AuthType.Advanced
        )
        viewState.close()
        viewState.callOnAdd(WalletItem.create(secretStorage, AddressBalanceTotal(address)))
    }
}