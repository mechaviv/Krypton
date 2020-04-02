package game.script;

/**
 * Created by MechAviv on 3/25/2020.
 */
public class ParamType {
    public static final int
            None = 0,
            NotCancellable          = 0x1,
            PlayerAsSpeaker         = 0x2,
            OverrideSpeakerID       = 0x4,
            SpeakerOnRight          = 0x8;
}
