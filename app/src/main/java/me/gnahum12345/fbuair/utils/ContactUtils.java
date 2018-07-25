package me.gnahum12345.fbuair.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class ContactUtils {

    /** codes for options after adding contact */
    final public static int SUCCESS = 0;
    final public static int PHONE_CONFLICT = 1;
    final public static int EMAIL_CONFLICT = 2;

    /** conflict object with type of conflict (email or phone) and id of conflict if applicable */
    public static class AddContactResult {
        int resultCode;
        String contactId;

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }
    }

    /** adds given user to contacts and returns contact id and raw contact id */
    public static String[] addContact(Context context, User user) {
        String contactId = "";
        String rawContactId = "";

        // fake image
        Bitmap profileImageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.happy_face);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] profileImageBytes = stream.toByteArray();
        profileImageBitmap.recycle();

        // start adding contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        // add name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, user.getName())
                .build());
        // add photo
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Photo.PHOTO, profileImageBytes)
                .build());
        // add organization
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Organization.TITLE, user.getOrganization())
                .build());
        // add number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Phone.NUMBER, user.getPhoneNumber())
                .withValue(CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        // add email
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Email.ADDRESS, user.getEmail())
                .withValue(CommonDataKinds.Email.TYPE, CommonDataKinds.Email.TYPE_HOME)
                .build());

        try {
            // create the new contact
            ContentProviderResult[] results = context.getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
            // get created raw contact ID (for undoing)
            rawContactId = String.valueOf(ContentUris.parseId(results[0].uri));
            // get created contact ID (for viewing)
            final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};
            final Cursor cursor = context.getContentResolver().query(results[0].uri, projection, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                contactId = String.valueOf(cursor.getLong(0));
                cursor.close();
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            return new String[]{};
        }
        return new String[]{contactId, rawContactId};
    }

    /** undoes raw contact operation */
    public static void undo(Context context, String rawContactId) {
        // delete raw contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts._ID + " = ?",
                        new String[] {rawContactId})
                .build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /** creates intent to view given contact */
    public static void viewContact(Context context, String contactId) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(contactUri);
        context.startActivity(intent);
    }

    /** returns ID of contact matching phone number. if doesn't exist, returns null */
    public static String getPhoneConflictId(Context context, String phone) {
        // get URI path for given phone number
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        // set columns to retrieve
        String[] phoneNumberProjection = {ContactsContract.PhoneLookup._ID};
        // lookup phone number
        Cursor cursor = context.getContentResolver().query(lookupUri, phoneNumberProjection,
                null, null, null);
        // if there is a result, return ID of matching contact
        if (cursor != null && cursor.moveToFirst()) {
            String id = String.valueOf(cursor.getLong(0));
            cursor.close();
            return id;
        }
        // return null if no matching contacts
        return null;
    }

    /** returns ID of contact matching email address. returns null if no conflict/match */
    public static String getEmailConflictId(Context context, String email) {
        // get URI path for given email
        Uri lookupUri = Uri.withAppendedPath(CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(email));
        // set columns to retrieve
        String[] emailProjection = {CommonDataKinds.Email.CONTACT_ID};
        // lookup email
        Cursor cursor = context.getContentResolver().query(lookupUri, emailProjection,
                null, null, null);
        // if there is a result, return ID of matching contact
        if (cursor != null && cursor.moveToFirst()) {
            String id = String.valueOf(cursor.getLong(0));
            cursor.close();
            return id;
        }
        // return null if no matching contacts
        return null;
    }

    /** checks for contact conflicts and returns result code */
    public static AddContactResult findConflict(Context context, User user) {
        AddContactResult addContactResult = new AddContactResult();
        // find phone number conflicts
        final String phoneConflictId = getPhoneConflictId(context, user.getPhoneNumber());
        if (phoneConflictId != null) {
            addContactResult.setResultCode(PHONE_CONFLICT);
            addContactResult.setContactId(phoneConflictId);
        }
        // find email conflicts
        final String emailConflictId = getEmailConflictId(context, user.getEmail());
        if (emailConflictId != null) {
            addContactResult.setResultCode(EMAIL_CONFLICT);
            addContactResult.setContactId(emailConflictId);
        }
        // return success if no conflicts
        addContactResult.setResultCode(SUCCESS);
        return addContactResult;
    }

    /** checks if there are multiple raw contacts for one contact ID (for notifying user of merge) */
    public static boolean mergeOccurred(Context context, String contactId) {
        // get URI path for given ID
        Uri lookupUri = ContactsContract.RawContacts.CONTENT_URI;
        // no columns needed
        String[] projection = {ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts._ID};
        // find where contact ID = user's contact id
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=" + contactId;
        // lookup contact ID
        Cursor cursor = context.getContentResolver().query(lookupUri, projection, selection,
                null, null);
        // if there are multiple results, ID existed before adding contact and merge occured
        if (cursor != null && cursor.getCount() > 1) {
            cursor.close();
            return true;
        }
        return false;
    }
}
