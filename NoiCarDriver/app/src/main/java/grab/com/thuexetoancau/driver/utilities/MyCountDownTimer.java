package grab.com.thuexetoancau.driver.utilities;

import android.os.CountDownTimer;

import static grab.com.thuexetoancau.driver.utilities.Global.countDownTimer;

/**
 * Created by DatNT on 8/15/2017.
 */

public class MyCountDownTimer extends CountDownTimer
{
    private long timePassed = 0;

    public MyCountDownTimer(long startTime, long interval)
    {
        super(startTime, interval);
    }

    @Override
    public void onFinish()
    {

    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        timePassed++;
    }

    public long getTimePassed()
    {
        return timePassed;
    }
}