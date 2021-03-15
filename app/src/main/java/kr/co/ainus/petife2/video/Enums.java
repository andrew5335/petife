package kr.co.ainus.petife2.video;

public class Enums {
    public Enums() {
    }

    public static enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        ERROR,
        QUIET;

        private LogLevel() {
        }
    }

    public static enum Rotation {
        NORMAL,
        ROTATION_90,
        ROTATION_180,
        ROTATION_270;

        private Rotation() {
        }
    }

    public static enum Resolution {
        RES_QVGA,
        RES_VGA,
        RES_720X480,
        RES_720X576,
        RES_720P,
        RES_1080P;

        private Resolution() {
        }
    }

    public static enum Pipe {
        H264_PRIMARY,
        H264_SECONDARY,
        MJPEG_PRIMARY,
        MJPEG_SECONDARY;

        private Pipe() {
        }
    }

    public static enum Action {
        UPDATE_PASSWORD,
        JOIN_WIFI,
        CHECK_PASSWORD,
        CHANGE_VIDEO_RESOLUTION;

        private Action() {
        }
    }

    public static enum ScaleType {
        CENTER_INSIDE,
        CENTER_CROP;

        private ScaleType() {
        }
    }

    public static enum Transport {
        TCP,
        UDP;

        private Transport() {
        }
    }

    public static enum State {
        IDLE,
        PREPARING,
        PLAYING,
        STOPPED;

        private State() {
        }
    }
}
