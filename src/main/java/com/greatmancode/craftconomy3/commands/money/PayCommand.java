/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import com.greatmancode.tools.utils.Tools;

public class PayCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Common.getInstance().getAccountManager().exist(args[0], false)) {
            if (Tools.isValidDouble(args[1])) {
                double amount = Double.parseDouble(args[1]);
                Currency currency = Common.getInstance().getCurrencyManager().getDefaultCurrency();
                if (args.length > 2) {
                    if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
                        currency = Common.getInstance().getCurrencyManager().getCurrency(args[2]);
                    } else {
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
                        return;
                    }
                }
                boolean hasEnough = Common.getInstance().getAccountManager().getAccount(sender, false).hasEnough(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName());

                if (hasEnough) {
                    Common.getInstance().getAccountManager().getAccount(sender, false).withdraw(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName(), Cause.PAYMENT, args[0]);
                    Common.getInstance().getAccountManager().getAccount(args[0], false).deposit(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName(), Cause.PAYMENT, sender);
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("money_pay_sent", Common.getInstance().format(null, currency, amount), args[0]));
                    if (Common.getInstance().getServerCaller().getPlayerCaller().isOnline(args[0])) {
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(args[0], Common.getInstance().getLanguageManager().parse("money_pay_received", Common.getInstance().format(null, currency, amount), sender));
                    }
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("not_enough_money"));
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("player_not_exist"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("money_pay_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.money.pay";
    }
}
