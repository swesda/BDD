package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.CardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferCardValue;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    @BeforeEach
    void login() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void returnCardBalancesToDefault() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
        var cardPage = new CardPage();
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        if (firstCardBalance < secondCardBalance) {
            cardPage.firstCardButtonClick();
            var transferCardValue = new TransferCardValue();
            transferCardValue.validTransfer(String.valueOf((secondCardBalance - firstCardBalance) / 2),
                    DataHelper.getSecondCard().getNumber());
        } else if (firstCardBalance > secondCardBalance) {
            cardPage.secondCardButtonClick();
            var transferCardValue = new TransferCardValue();
            transferCardValue.validTransfer(String.valueOf((firstCardBalance - secondCardBalance) / 2),
                    DataHelper.getFirstCard().getNumber());
        }
    }

    @AfterEach
    void closeWebBrowser() {
        closeWebDriver();
    }

    @Test
    void shouldTransferFromSecondCardToFirst() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue ();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 2500;
        transferCardValue.validTransfer(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        var cardPage = new CardPage();
        cardPage.secondCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0002"));
        var amount = 5000;
        transferCardValue.validTransfer(String.valueOf(amount), DataHelper.getFirstCard().getNumber());
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldTransferFromSecondCardToFirstFractionalAmountLess1000RUB() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 150.50;
        transferCardValue.validTransfer(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldTransferFromSecondCardToFirstFractionalAmountMore1000RUB() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 4500.50;
        transferCardValue.validTransfer(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldNotTransferFromNotExistingCardToFirst() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 6000;
        transferCardValue.invalidTransfer(String.valueOf(amount), "5559 0000 0000 0003");
    }

    @Test
    void shouldNotTransferFromSecondCardToFirstOverCardBalance() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 40000;
        transferCardValue.invalidTransfer(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
    }

    @Test
    void shouldCancelTransfer() {
        var cardPage = new CardPage();
        cardPage.firstCardButtonClick();
        var transferCardValue = new TransferCardValue();
        transferCardValue.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 6000;
        transferCardValue.cancelTransfer(String.valueOf(amount), DataHelper.getSecondCard().getNumber());
        var firstCardBalance = cardPage.getCardBalance(DataHelper.getFirstCard().getDataTestId());
        var secondCardBalance = cardPage.getCardBalance(DataHelper.getSecondCard().getDataTestId());
        assertEquals(10000, firstCardBalance);
        assertEquals(10000, secondCardBalance);
    }

}
