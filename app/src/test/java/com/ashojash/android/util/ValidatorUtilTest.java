package com.ashojash.android.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ValidatorUtilTest {
    @Test
    public void should_validate_username() {
        String username;
        int validationCode;
        username = "invalidString !@#$%^&*()";
        validationCode = ValidatorUtil.validateUsername(username);
        assertThat(validationCode, is(ValidatorUtil.REG_NOT_MATCH));

        username = "valid-Username0";
        validationCode = ValidatorUtil.validateUsername(username);
        assertThat(validationCode, is(ValidatorUtil.FIELD_OK));
    }

    @Test
    public void should_validate_login() {
        String login;
        int validationCode;
        login = "usernameOrPassword";
        validationCode = ValidatorUtil.validateLogin(login);
        assertThat(validationCode, is(ValidatorUtil.FIELD_OK));
    }

    @Test
    public void should_validate_name() {
        String name = "اسم و فامیل";
        boolean isTextInPersian = ValidatorUtil.isTextInPersian(name, false);
        assertThat(isTextInPersian, is(true));
    }

    @Test
    public void should_validate_ordinary_sentences() {
        String sentence = "غذاش خوب بود... باید اینجارو امتحان کنید؟! من گفتم";
        boolean isSentenceInPersian = ValidatorUtil.isTextInPersian(sentence, true);
        assertThat(isSentenceInPersian, is(true));
    }

    @Test
    public void should_validate_review() {
        String sentence = "این یک مرور تیپیکال هست که فرض میکنیم من به رستوران رفتم و غذای #نوع-1 و #نوع_2! رو خوردم.\n" +
                "با دوستم @mehrdad@hasan و @mehrdad2@mehrdad رفته بودم.\n" +
                "و این کاراکترهای _:\"()!# رو به کار بردم!";
        boolean isSentenceInPersian = ValidatorUtil.isReviewInPersian(sentence);
        assertThat(isSentenceInPersian, is(true));
    }
}
