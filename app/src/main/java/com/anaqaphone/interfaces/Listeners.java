package com.anaqaphone.interfaces;
public interface Listeners {
    interface BackListener
    {
        void back();
    }
    interface LoginListener{
        void validate();
        void showCountryDialog();
    }

    interface SignUpListener{

        void openSheet();
        void closeSheet();
        void checkDataValid();
        void checkReadPermission();
        void checkCameraPermission();
    }

    interface SettingActions
    {
        void news();
        void openWhatsApp();
        void terms();
        void aboutApp();
        void contactUs();
        void changeLanguage();
        void logout();
        void openTwitter();
        void openFacebook();
        void openInstagram();
        void favorite();

    }

    interface PaymentTypeAction
    {
        void onCredit();
        void onPaypal();
        void onCash();
        void onNext();
        void onPrevious();
    }

    interface NextPreviousAction
    {
        void onNext();
        void onPrevious();
    }
    interface UpdateProfileListener
    {
        void updateProfile();
    }





}
