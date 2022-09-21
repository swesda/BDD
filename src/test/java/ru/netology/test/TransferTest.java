package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.CardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    CardPage cardPage;

    @BeforeEach
    public void openPage() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        cardPage = verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    public void returnCardBalancesToDefault() {
        var balance = cardPage.getCardBalance(DataHelper.getFirstCard());
        if (balance > 10000) {
            var difference = balance - 10000;
            var transferPade = cardPage.transferCardValue(DataHelper.getSecondCard());
            transferPade.transferMoney(DataHelper.getFirstCard(), String.valueOf(difference));

        } else {
            var difference = 10000 - balance;
            var transferPade = cardPage.transferCardValue(DataHelper.getFirstCard());
            transferPade.transferMoney(DataHelper.getSecondCard(), String.valueOf(difference));
        }
    }

    @Test
    void shouldTransferFromSecondCardToFirst () {
        var transferPage = cardPage.transferCardValue(DataHelper.getFirstCard());
        transferPage.transferMoney(DataHelper.getSecondCard(), "2500");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(12500, balance0);
        assertEquals(7500, balance1);
    }

    @Test
    void shouldTransferFromFirstCardToSecond () {
        var transferPage = cardPage.transferCardValue(DataHelper.getSecondCard());
        transferPage.transferMoney(DataHelper.getFirstCard(), "200");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(9800, balance0);
        assertEquals(10200, balance1);
    }

    @Test
    void shouldTransferFromSecondCardToFirstFractionalAmountLess1000RUB () {
        var transferPage = cardPage.transferCardValue(DataHelper.getFirstCard());
        transferPage.transferMoney(DataHelper.getSecondCard(), "150.5");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(10150.5, balance0);
        assertEquals(9849.5, balance1);
    }

    @Test
    void shouldTransferFromSecondCardToFirstFractionalAmountMore1000RUB () {
        var transferPage = cardPage.transferCardValue(DataHelper.getFirstCard());
        transferPage.transferMoney(DataHelper.getSecondCard(), "4500.5");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(14500.5, balance0);
        assertEquals(5499.5, balance1);
    }

    @Test
    void shouldNullTransferMoneyFromOneCardToAnother() {
        var transferPage = cardPage.transferCardValue(DataHelper.getFirstCard());
        transferPage.transferMoney(DataHelper.getSecondCard(), "0");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(10000, balance0);
        assertEquals(10000, balance1);
    }

    @Test
    void shouldNotTransferFromSecondCardToFirstOverCardBalance () {
        var transferPage = cardPage.transferCardValue(DataHelper.getFirstCard());
        transferPage.transferMoney(DataHelper.getSecondCard(), "40000");
        var balance0 = cardPage.getCardBalance(DataHelper.getFirstCard());
        var balance1 = cardPage.getCardBalance(DataHelper.getSecondCard());

        assertEquals(10000, balance0);
        assertEquals(10000, balance1);
    }
}