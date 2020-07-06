package org.d3ifcool.addictcontrol.Account;


public class Account {
    private  String mUsername;
    private  String mImage ;
    private  String mTypeAccount;
    private String mQuote;


    /**Constructor of account
     *
     * @param mUsername is username for account
     * @param mImage is to save avatar image
     * @param mTypeAccount is to check if user has been register
     * @param mQuote is to save quote from user
     */

    public Account(String mUsername, String mImage, String mTypeAccount, String mQuote) {
        this.mUsername = mUsername;
        this.mImage = mImage;
        this.mTypeAccount = mTypeAccount;
        this.mQuote = mQuote;
    }



    public String getmUsername() {
        return mUsername;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmTypeAccount() {
        return mTypeAccount;
    }

    public String getQuote() {
        return mQuote;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public void setQuote(String quote) {
        this.mQuote = quote;
    }
}
