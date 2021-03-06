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

package network.minter.bipwallet.auth.views;

import android.view.View;

import javax.inject.Inject;

import moxy.InjectViewState;
import network.minter.bipwallet.auth.contract.AuthView;
import network.minter.bipwallet.internal.di.annotations.FragmentScope;
import network.minter.bipwallet.internal.mvp.MvpBasePresenter;

/**
 * minter-android-wallet. 2018
 *
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
@InjectViewState
@FragmentScope
public class AuthPresenter extends MvpBasePresenter<AuthView> {

    @Inject
    public AuthPresenter() {
    }

    @Override
    public void attachView(AuthView view) {
        super.attachView(view);
        getViewState().setOnClickCreateWallet(this::onClickCreateWallet);
        getViewState().setOnClickSignIn(this::onClickSignIn);
        getViewState().setOnHelp(this::onClickHelp);
    }

    private void onClickSignIn(View view) {
        getViewState().startSignIn();
    }

    private void onClickHelp(View view) {
        getViewState().startHelp();
    }

    private void onClickCreateWallet(View view) {
        getViewState().startCreateWallet();
    }
}
