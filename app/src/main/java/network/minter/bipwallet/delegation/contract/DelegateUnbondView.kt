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

package network.minter.bipwallet.delegation.contract

import android.text.TextWatcher
import android.view.View
import androidx.annotation.StringRes
import com.edwardstock.inputfield.form.InputWrapper
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import network.minter.bipwallet.delegation.ui.ValidatorSelectorActivity
import network.minter.bipwallet.internal.dialogs.DialogExecutor
import network.minter.bipwallet.sending.account.SelectorData
import network.minter.core.crypto.MinterHash
import network.minter.core.crypto.MinterPublicKey
import network.minter.explorer.models.BaseCoinValue
import network.minter.explorer.models.ValidatorItem

/**
 * minter-android-wallet. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface DelegateUnbondView : MvpView {
    fun onError(t: Throwable?)
    fun onError(err: CharSequence?)
    fun setValidator(validator: ValidatorItem, onInflated: (View) -> Unit)
    fun setValidator(validator: MinterPublicKey, onInflated: (View) -> Unit)
    fun setValidatorRaw(validator: MinterPublicKey)
    fun setOnValidatorSelectListener(listener: View.OnClickListener)
    fun setOnAccountSelectListener(listener: View.OnClickListener)

    fun startValidatorSelector(requestCode: Int, filter: ValidatorSelectorActivity.Filter = ValidatorSelectorActivity.Filter.Online)
    fun startAccountSelector(items: List<SelectorData<BaseCoinValue>>, listener: (SelectorData<BaseCoinValue>) -> Unit)
    fun setAccountTitle(accountName: CharSequence?)
    fun setTitle(@StringRes resId: Int)
    fun setSubtitle(@StringRes resId: Int)
    fun setFee(fee: CharSequence)
    fun setOnClickUseMax(listener: View.OnClickListener)
    fun setAmount(amount: String)
    fun setTextChangedListener(listener: (input: InputWrapper, valid: Boolean) -> Unit)
    fun setEnableSubmit(enable: Boolean)
    fun setAccountError(error: CharSequence?)
    fun setOnSubmitListener(listener: View.OnClickListener)
    fun setValidatorsAutocomplete(items: List<ValidatorItem>, listener: (ValidatorItem) -> Unit)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun startDialog(executor: DialogExecutor)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun startExplorer(txHash: MinterHash)

    fun finishSuccess()
    fun finishCancel()

    fun setOnValidatorOverlayClickListener(listener: (View) -> Unit)
    fun hideValidatorOverlay()
    fun addValidatorTextChangeListener(textWatcher: TextWatcher)
    fun setValidatorError(message: CharSequence?)
    fun setOnValidatorSelectListener(onClick: (View) -> Unit)
    fun setValidatorSelectDisabled()
    fun setCoinLabel(labelRes: Int)
    fun setEnableValidator(enable: Boolean)
    fun setValidatorSelectSuffix(listener: (View) -> Unit)
    fun setValidatorClearSuffix(listener: (View) -> Unit)
    fun clearValidatorInput()

}