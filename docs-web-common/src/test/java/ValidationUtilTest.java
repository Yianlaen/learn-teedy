import com.sismics.rest.exception.ClientException;
import com.sismics.rest.util.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Tests for parameter validation helpers.
 */
public class ValidationUtilTest {
    @Test
    public void validateRequiredRejectsNullAndAcceptsValue() {
        ValidationUtil.validateRequired("value", "field");

        ClientException exception = Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateRequired(null, "field"));
        Assertions.assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    public void validateLengthCoversTrimNullableAndBounds() {
        Assertions.assertEquals("abc", ValidationUtil.validateLength("  abc  ", "field", 2, 4));
        Assertions.assertEquals("", ValidationUtil.validateLength("   ", "field", 2, 4, true));

        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateLength(null, "field", 1, 3));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateLength("a", "field", 2, null));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateLength("abcd", "field", null, 3));
    }

    @Test
    public void validateTextPatternsAcceptValidValuesAndRejectInvalidValues() {
        ValidationUtil.validateEmail("person@example.com", "email");
        ValidationUtil.validateHexColor("#123456", "color", false);
        ValidationUtil.validateTagName("tag_name");
        Assertions.assertEquals("https://example.com/path",
                ValidationUtil.validateHttpUrl(" https://example.com/path ", "url"));
        ValidationUtil.validateAlphanumeric("abc_123", "code");
        ValidationUtil.validateUsername("john.doe@example.com", "username");
        ValidationUtil.validateRegex("DOC-123", "document", "DOC-[0-9]+");

        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateEmail("not-an-email", "email"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateHexColor("#12345", "color", false));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateTagName("bad tag"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateTagName("bad:tag"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateHttpUrl("ftp://example.com", "url"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateAlphanumeric("abc-123", "code"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateUsername("john doe", "username"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateRegex("DOC-abc", "document", "DOC-[0-9]+"));
    }

    @Test
    public void validateNumbersAndDatesAcceptValidValuesAndRejectInvalidValues() {
        Assertions.assertEquals(Integer.valueOf(42), ValidationUtil.validateInteger("42", "count"));
        Assertions.assertEquals(Long.valueOf(1234567890123L), ValidationUtil.validateLong("1234567890123", "size"));

        Date epoch = ValidationUtil.validateDate("0", "created", false);
        Assertions.assertEquals(0L, epoch.getTime());
        Assertions.assertNull(ValidationUtil.validateDate("", "created", true));

        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateInteger("forty-two", "count"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateLong("large-value", "size"));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateDate(null, "created", false));
        Assertions.assertThrows(ClientException.class,
                () -> ValidationUtil.validateDate("today", "created", false));
    }
}
