package kr.co.ainus.petife2.listener;

public interface SignInListener {
    public void onSignIn(SignInResultType signInResult);

    public enum SignInResultType {
        SUCCESS,
        FAIL
    }
}
