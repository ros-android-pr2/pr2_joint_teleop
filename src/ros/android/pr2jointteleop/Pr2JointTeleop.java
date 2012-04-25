package ros.android.pr2jointteleop;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;
import org.ros.exception.RosException;
import org.ros.namespace.NameResolver;
import ros.android.activity.RosAppActivity;
import ros.android.views.SensorImageView;
import android.net.Uri;
import java.io.File;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.content.Context;
import java.lang.String;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TableLayout;
import org.ros.message.sensor_msgs.Joy;
import org.ros.message.sensor_msgs.JointState;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.lang.String;
import java.lang.Math;
import java.lang.Integer;
import java.lang.Double;
import org.ros.message.Message;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.message.MessageListener;
import org.ros.message.Time;

public class Pr2JointTeleop extends RosAppActivity {
   private int mode = 1;
   private int arm = 0;
   private static int INDIVIDUAL = 0;
   private static int MIRROR = 1; 
   private Joy joyMessage;
   private String topic = "sliderJoy";
   private boolean sendMessages = true;
   private boolean nullMessage = true;
   //private int seq = 0;
   private Thread pubThread;
   private Publisher<Joy> joyPub;
   private Subscriber<JointState> jointStateSub;
   private TableLayout rightArm;
   private TextView leftArmTitle;

    @Override
  public void onCreate(Bundle savedInstanceState) {
      setDefaultAppName("pr2_joint_teleop_app/pr2_joint_teleop");
      setDashboardResource(R.id.top_bar);
      setMainWindowResource(R.layout.main);
      super.onCreate(savedInstanceState);
      
      rightArm = (TableLayout) findViewById(R.id.right_arm_table);
      leftArmTitle = (TextView) findViewById(R.id.left_arm_title);
 
       topic = "sliderJoy";
       joyMessage = new Joy();
       joyMessage.axes = new float[19];
       joyMessage.buttons = new int[25];
       joyMessage.buttons[24] = 1;
       SeekBar seekBarLeft1 = (SeekBar)findViewById(R.id.seekbarLeft1);
       SeekBar seekBarLeft2 = (SeekBar)findViewById(R.id.seekbarLeft2);
       SeekBar seekBarLeft3 = (SeekBar)findViewById(R.id.seekbarLeft3);
       SeekBar seekBarLeft4 = (SeekBar)findViewById(R.id.seekbarLeft4);
       SeekBar seekBarLeft5 = (SeekBar)findViewById(R.id.seekbarLeft5);
       SeekBar seekBarLeft6 = (SeekBar)findViewById(R.id.seekbarLeft6);
       SeekBar seekBarLeft7 = (SeekBar)findViewById(R.id.seekbarLeft7);
       SeekBar seekBarLeft8 = (SeekBar)findViewById(R.id.seekbarLeft8);

       SeekBar seekBarRight1 = (SeekBar)findViewById(R.id.seekbarRight1);
       SeekBar seekBarRight2 = (SeekBar)findViewById(R.id.seekbarRight2);
       SeekBar seekBarRight3 = (SeekBar)findViewById(R.id.seekbarRight3);
       SeekBar seekBarRight4 = (SeekBar)findViewById(R.id.seekbarRight4);
       SeekBar seekBarRight5 = (SeekBar)findViewById(R.id.seekbarRight5);
       SeekBar seekBarRight6 = (SeekBar)findViewById(R.id.seekbarRight6);
       SeekBar seekBarRight7 = (SeekBar)findViewById(R.id.seekbarRight7);
       SeekBar seekBarRight8 = (SeekBar)findViewById(R.id.seekbarRight8);

       SeekBar seekBarHead1 = (SeekBar)findViewById(R.id.seekbarHead1);
       SeekBar seekBarHead2 = (SeekBar)findViewById(R.id.seekbarHead2);
       SeekBar seekBarTorso = (SeekBar)findViewById(R.id.seekbarTorso);

       final TextView seekBarLeftValue1 = (TextView)findViewById(R.id.seekbarLeftvalue1);
       final TextView seekBarLeftValue2 = (TextView)findViewById(R.id.seekbarLeftvalue2);
       final TextView seekBarLeftValue3 = (TextView)findViewById(R.id.seekbarLeftvalue3);
       final TextView seekBarLeftValue4 = (TextView)findViewById(R.id.seekbarLeftvalue4);
       final TextView seekBarLeftValue5 = (TextView)findViewById(R.id.seekbarLeftvalue5);
       final TextView seekBarLeftValue6 = (TextView)findViewById(R.id.seekbarLeftvalue6);
       final TextView seekBarLeftValue7 = (TextView)findViewById(R.id.seekbarLeftvalue7);
       final TextView seekBarLeftValue8 = (TextView)findViewById(R.id.seekbarLeftvalue8);

       final TextView seekBarRightValue1 = (TextView)findViewById(R.id.seekbarRightvalue1);
       final TextView seekBarRightValue2 = (TextView)findViewById(R.id.seekbarRightvalue2);
       final TextView seekBarRightValue3 = (TextView)findViewById(R.id.seekbarRightvalue3);
       final TextView seekBarRightValue4 = (TextView)findViewById(R.id.seekbarRightvalue4);
       final TextView seekBarRightValue5 = (TextView)findViewById(R.id.seekbarRightvalue5);
       final TextView seekBarRightValue6 = (TextView)findViewById(R.id.seekbarRightvalue6);
       final TextView seekBarRightValue7 = (TextView)findViewById(R.id.seekbarRightvalue7);
       final TextView seekBarRightValue8 = (TextView)findViewById(R.id.seekbarRightvalue8);


       final TextView seekBarValueHead1 = (TextView)findViewById(R.id.seekbarvalueHead1);
       final TextView seekBarValueHead2 = (TextView)findViewById(R.id.seekbarvalueHead2);
       final TextView seekBarValueTorso = (TextView)findViewById(R.id.seekbarvalueTorso);

       Spinner spinner = (Spinner) findViewById(R.id.spinner);
           ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.planets_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());


       seekBarLeft1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[9] = convert(progress);
    seekBarLeftValue1.setText(String.valueOf(joyMessage.axes[9]));
    
      sendMessages = true;
      nullMessage = false;
    } else {
    setJoyMessage(progress,9,seekBarLeftValue1);
    setJoyMessage(progress,17,seekBarLeftValue1);
    }
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
     // TODO Auto-generated method stub
     if (arm == INDIVIDUAL) {
       joyMessage.axes[0] = convert(progress);
       seekBarLeftValue2.setText(String.valueOf(joyMessage.axes[0]));
       sendMessages = true;
       nullMessage = false;
     } else {
         setJoyMessage(progress,0,seekBarLeftValue2);
         setJoyMessage(progress,8,seekBarLeftValue2);
     }
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[10] = convert(progress);
    seekBarLeftValue3.setText(String.valueOf(joyMessage.axes[10]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,10,seekBarLeftValue3);
         setJoyMessage(progress,16,seekBarLeftValue3);
    }      
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[1] = convert(progress);
    seekBarLeftValue4.setText(String.valueOf(joyMessage.axes[1]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,1,seekBarLeftValue4);
         setJoyMessage(progress,7,seekBarLeftValue4);
    }

   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[11] = convert(progress);
    seekBarLeftValue5.setText(String.valueOf(joyMessage.axes[11]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,11,seekBarLeftValue5);
         setJoyMessage(progress,15,seekBarLeftValue5);
    }
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }       
       });
       seekBarLeft6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[2] = convert(progress);
    seekBarLeftValue6.setText(String.valueOf(joyMessage.axes[2]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,2,seekBarLeftValue6);
         setJoyMessage(progress,6,seekBarLeftValue6);
    }
   }
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) { 
    joyMessage.axes[12] = convert(progress);
    seekBarLeftValue7.setText(String.valueOf(joyMessage.axes[12]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,12,seekBarLeftValue7);
         setJoyMessage(progress,14,seekBarLeftValue7);
    }

   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarLeft8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    if (arm == INDIVIDUAL) {
    joyMessage.axes[3] = convert(progress);
    seekBarLeftValue8.setText(String.valueOf(joyMessage.axes[3]));
      sendMessages = true;
      nullMessage = false;
    } else {
         setJoyMessage(progress,3,seekBarLeftValue8);
         setJoyMessage(progress,5,seekBarLeftValue8);
    }
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 17, seekBarRightValue1);
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 8, seekBarRightValue2);
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 16, seekBarRightValue3);
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 7, seekBarRightValue4);
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 15, seekBarRightValue5);
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });

   seekBarRight6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 6, seekBarRightValue6);
   }
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

       });

   seekBarRight7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 14, seekBarRightValue7);
   }
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

       });

   seekBarRight8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    setJoyMessage(progress, 5, seekBarRightValue8);
   }
   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

       });

   seekBarHead1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    joyMessage.axes[13] = convert(progress);
    seekBarValueHead1.setText(String.valueOf(joyMessage.axes[13]));
      sendMessages = true;
      nullMessage = false;
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarHead2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    joyMessage.axes[4] = convert(progress);
    seekBarValueHead2.setText(String.valueOf(joyMessage.axes[4]));
      sendMessages = true;
      nullMessage = false;
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
       seekBarTorso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress,
     boolean fromUser) {
    // TODO Auto-generated method stub
    joyMessage.axes[18] = convert(progress);
    seekBarValueTorso.setText(String.valueOf(joyMessage.axes[18]));
      sendMessages = true;
      nullMessage = false;
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
   }
       });
   }

  @Override
  protected void onNodeDestroy(Node node) {
    super.onNodeDestroy(node);
    stop();
  }

  @Override
  protected void onNodeCreate(Node node) {
    super.onNodeCreate(node);
    try {
      NameResolver appNamespace = getAppNamespace(node);
      start(node);
    } catch (RosException ex) {
      Toast.makeText(Pr2JointTeleop.this, "Failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
    jointStateSub = node.newSubscriber("joint_states","sensor_msgs/JointState");
    jointStateSub.addMessageListener(new MessageListener<org.ros.message.sensor_msgs.JointState>() {
            @Override
            public void onNewMessage(final org.ros.message.sensor_msgs.JointState data) {
              if (jointStateSub != null) {
                jointStateSub.shutdown();
                jointStateSub = null;
              } 
             
              
              //float num = (position[12] * 2 / 0.3) - 1;
              //int prog = convertToPercent(num);
              //SeekBar torso = findViewById(R.id.seekbarTorso);
              //torso.setProgress(prog);
 
              
              runOnUiThread(new Runnable() {
              @Override
              public void run() {
              double[] position;
              position = data.position;

              setInitialSeekBar(position);
              }
            });
          }
        }
      );

  }

  public void setInitialSeekBar(double[] position) {

    //torso
    double num0 = (position[12] * 2.0 / 0.3) - 1.0;
    Log.i("Pr2JointTeleop", "Torso = " + Double.toString(num0));

    int prog0 = convertToPercent(num0);
    Log.i("Pr2JointTeleop", "Torso slider = " + Integer.toString(prog0));
    SeekBar torso = (SeekBar)findViewById(R.id.seekbarTorso);
    torso.setProgress(prog0);
    //head pan
    double num1 = scale(position[14], -160, 160);
    int prog1 = 100 - convertToPercent(num1);
    SeekBar headpan = (SeekBar)findViewById(R.id.seekbarHead1);
    headpan.setProgress(prog1);
    //head tilt
    double num2 = scale(position[15],-30,90);
    int prog2 = 100  - convertToPercent(num2);
    SeekBar headtilt = (SeekBar)findViewById(R.id.seekbarHead2);
    headtilt.setProgress(prog2);

    //LEFT
    //shoulder pan
    double num3 = scale(position[32], 0, 130);
    int prog3 = 100 - convertToPercent(num3); 
    Log.i("Pr2JointTeleop", "Torso = " + Double.toString(num3));
    Log.i("Pr2JointTeleop", "Torso slider = " + Integer.toString(prog3));
    SeekBar l_shoulder_pan = (SeekBar)findViewById(R.id.seekbarLeft1);
    l_shoulder_pan.setProgress(prog3);
    //shoulder lift
    double num4 = scale(position[33], -30, 80);
    int prog4 = 100 -  convertToPercent(num4);
    SeekBar l_shoulder_lift = (SeekBar)findViewById(R.id.seekbarLeft2);
    l_shoulder_lift.setProgress(prog4);
    //shoulder roll
    double num5 = scale(position[31], -44, 224);
    int prog5 = convertToPercent(num5);
    SeekBar l_shoulder_roll = (SeekBar)findViewById(R.id.seekbarLeft3);
    l_shoulder_roll.setProgress(prog5);
    //elbow
    double num6 = scale(position[35], -130, 0);
    int prog6 = convertToPercent(num6);
    SeekBar l_elbow = (SeekBar)findViewById(R.id.seekbarLeft4);
    l_elbow.setProgress(prog6);
    //forearm roll
    double num7 = scale(position[34], -180, 180);
    int prog7 = convertToPercent(num7);
    SeekBar l_forearm_roll = (SeekBar)findViewById(R.id.seekbarLeft5);
    l_forearm_roll.setProgress(prog7);
    //wrist lift
    double num8 = scale(position[36], -130, 0);
    int prog8 = convertToPercent(num8);
    SeekBar l_wrist_flex = (SeekBar)findViewById(R.id.seekbarLeft6);
    l_wrist_flex.setProgress(prog8);
    //wrist roll
    double num9 = scale(position[37], -180, 180);
    int prog9 = convertToPercent(num9);
    SeekBar l_wrist_roll = (SeekBar)findViewById(R.id.seekbarLeft7);
    l_wrist_roll.setProgress(prog9);
    //gripper
    //double num10 = scale(position
    //RIGHT
    //shoulder pan
    num3 = scale(position[18], -130, 0);
    prog3 = convertToPercent(num3); 
    SeekBar r_shoulder_pan = (SeekBar)findViewById(R.id.seekbarRight1);
    r_shoulder_pan.setProgress(prog3);
    //shoulder lift
    num4 = scale(position[19], -30, 80);
    prog4 = 100 - convertToPercent(num4); 
    SeekBar r_shoulder_lift = (SeekBar)findViewById(R.id.seekbarRight2);
    r_shoulder_lift.setProgress(prog4);
    //shoulder roll
    num5 = scale(position[17], -224, 44);
    prog5 = convertToPercent(num5); 
    SeekBar r_shoulder_roll = (SeekBar)findViewById(R.id.seekbarRight3);
    r_shoulder_roll.setProgress(prog5);
    //elbow
    num6 = scale(position[21], -130, 0);
    prog6 = convertToPercent(num6); 
    SeekBar r_elbow = (SeekBar)findViewById(R.id.seekbarRight4);
    r_elbow.setProgress(prog6);
    //forearm roll
    num7 = scale(position[20], -180, 180);
    prog7 = convertToPercent(num7); 
    SeekBar r_forearm_roll = (SeekBar)findViewById(R.id.seekbarRight5);
    r_forearm_roll.setProgress(prog7);
    //wrist lift
    num8 = scale(position[22], -130, 0);
    prog8 = convertToPercent(num8); 
    SeekBar r_wrist_flex = (SeekBar)findViewById(R.id.seekbarRight6);
    r_wrist_flex.setProgress(prog8);
    //wrist roll
    num9 = scale(position[23], -180, 180);
    prog9 = convertToPercent(num9); 
    SeekBar r_wrist_roll = (SeekBar)findViewById(R.id.seekbarRight7);
    r_wrist_roll.setProgress(prog9);
    //gripper
    //double num10 = scale(position

  }

  public double scale(double position, int min, int max) {
    double num = ((180.0 * position / Math.PI) - (max + min)/2) * 2 / (max - min);
    return num;
  }

  public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent,
        View view, int pos, long id) {
      Toast.makeText(parent.getContext(), "Arm is: " +
          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
          //TableLayout rightArm = (TableLayout) view.findViewById(R.id.right_arm_table);
          //TextView leftArmTitle = (TextView) view.findViewById(R.id.left_arm_title);
          if (pos == 0) {
            arm = INDIVIDUAL;
            if (rightArm != null) {
              rightArm.setVisibility(View.VISIBLE);
              leftArmTitle.setText("Left Arm");
            }
          } else if (pos == 1) {
            arm = MIRROR;
            if (rightArm != null) {
              rightArm.setVisibility(View.GONE);
              leftArmTitle.setText("Both Arms");
            }
          } 
             
    }

    public void onNothingSelected(AdapterView parent) {
      // Do nothing.
    }
}

  public float convert(int prog) {
    float progFloat;
    progFloat = prog;
    progFloat = (progFloat / 50)- 1;
    return progFloat;
    }

  public int convertToPercent(double num) {
    double percent = (num + 1.0) * 50.0;
    int prog = (int) percent;
    return prog;
  }

  public void setJoyMessage(int progress, int index, TextView tview) {
    joyMessage.axes[index] = convert(progress);
    tview.setText(String.valueOf(joyMessage.axes[index]));
    sendMessages = true;
    nullMessage = false;
  }


  private <T extends Message> void createPublisherThread(final Publisher<T> pub, final T message,
      final int rate) {
    pubThread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          while (true) {
            if (sendMessages) {
              Log.i("Sliders", "send joy message");
              pub.publish(message);
              if (nullMessage) {
                sendMessages = false;
              }
            } else {
              Log.i("Sliders", "skipping joy");
            }
            Thread.sleep(1000 / rate);
          }
        } catch (InterruptedException e) {
        }
      }
    });
    Log.i("JoystickView", "started pub thread");
    pubThread.start();
  }

  public void start(Node node) throws RosException {
    Log.i("Sliders", "init joyPub");
    joyPub = node.newPublisher(topic, "sensor_msgs/Joy");
    createPublisherThread(joyPub, joyMessage, 10);
  }

  public void stop() {
    if (pubThread != null) {
      pubThread.interrupt();
      pubThread = null;
    }
    if (joyPub != null) {
      joyPub.shutdown();
      joyPub = null;
    }
  }
  

}
