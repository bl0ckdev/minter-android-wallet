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

package network.minter.bipwallet.wallets.data;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import network.minter.explorer.models.CoinBalance;

public class CoinBalanceDiffUtilImpl extends DiffUtil.Callback {
    private final List<CoinBalance> mOldList, mNewList;

    public CoinBalanceDiffUtilImpl(List<CoinBalance> oldList, List<CoinBalance> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        CoinBalance oldItem = mOldList.get(oldItemPosition);
        CoinBalance newItem = mNewList.get(newItemPosition);

        if (oldItem.address == null || newItem.address == null) {
            return false;
        }
        return oldItem.address.equals(newItem.address) && oldItem.coin.equals(newItem.coin);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        CoinBalance oldItem = mOldList.get(oldItemPosition);
        CoinBalance newItem = mNewList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
