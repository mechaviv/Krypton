package common.login;

/**
 * Created by MechAviv on 3/17/2020.
 */
public class LoginState {
    public static final int
            Invalid = 0,
            WaitCheckPassword = 1,
            WaitConfirmEULA = 2,
            WaitSelectGender = 3,
            WaitInsertPinCode = 4,
            WaitUpdatePinCode = 5,
            WaitSelectWorld = 6,
            WaitCenterSelectWorldResult = 7,
            WaitSelectCharacter = 8,
            WaitCenterSelectCharacterResult = 9,
            WaitViewAllCharacter = 10,
            WaitSelectCharacterByVAC = 11,
            Completed = 12,
            Disconnected = 13;
}
