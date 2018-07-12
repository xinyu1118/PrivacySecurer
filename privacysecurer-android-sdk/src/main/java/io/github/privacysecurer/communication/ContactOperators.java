package io.github.privacysecurer.communication;


import java.util.List;

import io.github.privacysecurer.core.Function;
import io.github.privacysecurer.core.Item;
import io.github.privacysecurer.utils.annotations.PSOperatorWrapper;

@PSOperatorWrapper
public class ContactOperators {

    /**
     * Get the email list from all contacts.
     *
     * @return the function
     */
    public static Function<Item, List<String>> getContactEmails() {
        String emailsField = Contact.EMAILS;
        return new ContactEmailGetter(emailsField);
    }

    /**
     * Get the phone list from all contacts.
     *
     * @return the function
     */
    public static Function<Item, List<String>> getContactPhones() {
        String phonesField = Contact.PHONES;
        return new ContactPhoneGetter(phonesField);
    }

    /**
     * Get contact lists.
     *
     * @return the function
     */
    public static Function<Item, List<Item>> getContactLists() {
        return new ContactListsGetter();
    }

}
