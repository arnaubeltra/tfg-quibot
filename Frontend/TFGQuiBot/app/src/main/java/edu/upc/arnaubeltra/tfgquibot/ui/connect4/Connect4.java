package edu.upc.arnaubeltra.tfgquibot.ui.connect4;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class Connect4 extends Fragment {

    private static boolean GAME_STARTED = false;
    private static boolean PLAYERS_READY = false;
    private static boolean YOUR_TURN = false;
    private static boolean IS_AUTHORIZED = false;
    private static boolean ROBOT_READY = false;
    private static boolean THREAD_RUNNING = false;
    private static boolean GAME_FINISHED = false;

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private Connect4ViewModel connect4ViewModel;

    private ConstraintLayout constraintLayout;
    private TextView txtInfoPlayer, txtInfoGame;
    private ImageView board;
    private Button btnColumn1, btnColumn2, btnColumn3, btnColumn4, btnColumn5, btnColumn6, btnNewGameConnect4;

    private int player = 0;
    private int init = 0, init1 = 0, init2 = 0;
    private int update = 0;
    private Boolean robotConnected = true;
    private int flag = 0, flag2 = 0;

    private ArrayList<Integer> circleObjects = new ArrayList<>();
    private View v;

    public Connect4() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_connect4, container, false);

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        connect4ViewModel = new ViewModelProvider(Login.getContext()).get(Connect4ViewModel.class);

        constraintLayout = v.findViewById(R.id.constraintScrollConnect4);

        txtInfoPlayer = v.findViewById(R.id.txtInfoPlayerConnect4);
        txtInfoGame = v.findViewById(R.id.txtInfoGameConnect4);
        txtInfoGame.setText(R.string.txtGameNotStarted);

        board = v.findViewById(R.id.boardConnect4);

        btnColumn1 = v.findViewById(R.id.btnColumn1);
        btnColumn1.setOnClickListener(view -> connect4Movement(1));
        btnColumn2 = v.findViewById(R.id.btnColumn2);
        btnColumn2.setOnClickListener(view -> connect4Movement(2));
        btnColumn3 = v.findViewById(R.id.btnColumn3);
        btnColumn3.setOnClickListener(view -> connect4Movement(3));
        btnColumn4 = v.findViewById(R.id.btnColumn4);
        btnColumn4.setOnClickListener(view -> connect4Movement(4));
        btnColumn5 = v.findViewById(R.id.btnColumn5);
        btnColumn5.setOnClickListener(view -> connect4Movement(5));
        btnColumn6 = v.findViewById(R.id.btnColumn6);
        btnColumn6.setOnClickListener(view -> connect4Movement(6));

        btnNewGameConnect4 = v.findViewById(R.id.btnNewGameConnect4);
        btnNewGameConnect4.setOnClickListener(view -> startFinishGame());
        v.findViewById(R.id.btnHowToPlayConnect4).setOnClickListener(view -> howToPlayDialog());

        THREAD_RUNNING = true;
        startThreadGame();

        checkRobotConnection();
        setupRequestResponseObserver();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionsViewModel.resetLiveData();
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        if (init2 == 0) {
            robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("robot-connection-failed")) {
                        if (flag == 1) { dialogWarningRobotNotConnected(); flag = 0; }
                        else flag = 1;
                    } else {
                        flag = 1;
                        setupPermissionsObserver();
                        if ((flag2 == 1) && init2 != 0) {
                            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "connect4"); flag2++;
                        } else if ((flag2 == 2) && init2 != 0)
                            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "connect4");
                        else flag2++;
                    } init2++;
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
    }

    private void dialogWarningRobotNotConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.txtRobotNotConnected)
                .setMessage(R.string.txtCheckRobotConnection)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupRequestResponseObserver() {
        connect4ViewModel.connect4RequestResponse();
        connect4ViewModel.getConnect4RequestResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);

                if (responseObject.getString("response").equals("connect4-start-success")) {
                    GAME_STARTED = true;
                    player = responseObject.getInt("player");
                    if (player == 1) txtInfoPlayer.setText(getResources().getString(R.string.txtVermelles));
                    else txtInfoPlayer.setText(getResources().getString(R.string.txtGrogues));
                    btnNewGameConnect4.setText(R.string.txtEndGame);
                    txtInfoGame.setText("");
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameStarted, Toast.LENGTH_SHORT).show();
                }

                else if (responseObject.getString("response").equals("game-is-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsFull, Toast.LENGTH_SHORT).show();

                else if (responseObject.getString("response").equals("waiting-for-player") && GAME_STARTED) {
                    txtInfoGame.setText(getResources().getString(R.string.txtWaitingForPlayer));
                    PLAYERS_READY = false;
                }

                else if (responseObject.getString("response").equals("connect4-init") && GAME_STARTED) {
                    PLAYERS_READY = true;
                    GAME_FINISHED = false;
                    if (player == 1) {
                        txtInfoGame.setText(getResources().getString(R.string.txtYourTurn));
                        YOUR_TURN = true;
                    } else if (player == 2) {
                        txtInfoGame.setText(getResources().getString(R.string.txtHisTurn));
                        YOUR_TURN = false;
                    }
                }

                else if (responseObject.getString("response").equals("no-winner") && GAME_STARTED)  {
                    if ((responseObject.getInt("player") != player)) {
                        //update = 1;
                        //onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), responseObject.getInt("player"));
                        txtInfoGame.setText(getResources().getString(R.string.txtYourTurn));
                        YOUR_TURN = true;
                    } else {
                        txtInfoGame.setText(getResources().getString(R.string.txtHisTurn));
                        YOUR_TURN = false;
                    }
                    if ((update == 0) && (responseObject.getInt("player") != player)) {
                        onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), responseObject.getInt("player"));
                        update = 1;
                    }

                    if ((responseObject.getInt("player") == player))
                        update = 0;

                }

                else if (responseObject.getString("response").equals("winner-1")) {
                    GAME_STARTED = false;
                    if (player == 1) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGameConnect4.setText(R.string.btnTxtNovaPartida);
                    GAME_FINISHED = true;
                }

                else if (responseObject.getString("response").equals("winner-2")) {
                    GAME_STARTED = false;
                    if (player == 2) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGameConnect4.setText(R.string.btnTxtNovaPartida);
                    GAME_FINISHED = true;
                }

                else if (responseObject.getString("response").equals("game-is-over") && GAME_STARTED) {
                    resetUIGameFinished();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("board-is-full") && GAME_STARTED) {
                    resetUIGameFinished();
                    connect4ViewModel.finishGameConnect4();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtBoardIsFull, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("connect4-position-success")) {
                    onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), player);
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtWaitRobot, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("connect4-position-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtColFull, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /*private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) {
                        if (init1 == 0) {
                            connect4ViewModel.startNewGameConnect4(Login.getIpAddress());
                            init1 = 1;
                        } else if (init1 == 1) init1 = 0;
                    } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtPermissionsPlayConnect4, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }*/

    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match"))
                        connect4ViewModel.startNewGameConnect4(Login.getIpAddress());
                    else Toast.makeText(getContext(), R.string.txtPermissionsPlayConnect4, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void startFinishGame() {
        if (!GAME_STARTED) {
            //setupPermissionsObserver();
            //permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "connect4");
            flag = 1;
            robotConnectionViewModel.checkRobotConnection();
            resetUIGameFinished();
        } else {
            GAME_FINISHED = true;
            btnNewGameConnect4.setText(R.string.btnTxtNovaPartida);
            txtInfoGame.setText(R.string.txtGameNotStarted);
            txtInfoPlayer.setText("");
            finishGame();
            connect4ViewModel.finishGameConnect4();
            Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_SHORT).show();
        }
    }

    private void startThreadGame() {
        new Thread(()-> {
            int i = 0;
            while (THREAD_RUNNING) {
                while (!GAME_STARTED) {
                    //Log.d("TAG", "GAME NOT STARTED: " + GAME_FINISHED + " " + i);
                    if (!THREAD_RUNNING) break;
                    //permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "tic_tac_toe");
                    if (GAME_FINISHED && i == 5) {
                        Log.d("TAG", "GAME FINISHED TRUE");
                        finishGame();
                        connect4ViewModel.finishGameConnect4();
                        GAME_FINISHED = false;
                    }
                    if (i == 5) i = 0;
                    else i++;

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (GAME_STARTED) {
                    if (!THREAD_RUNNING) break;
                    connect4ViewModel.connect4CheckStatus();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void connect4Movement(int column) {
        if (GAME_STARTED) {
            if (PLAYERS_READY) {
                if (YOUR_TURN) connect4ViewModel.connect4Position(player, column);
                else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtNotYourTurn, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtCantPlayWaitingOtherPlayer, Toast.LENGTH_SHORT).show();
        } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtFirstStartNewGame, Toast.LENGTH_SHORT).show();
    }

    private void onNewMovement(int row, int column, int player) {
        ImageView imageCircle = new ImageView(getContext());
        imageCircle.setId(View.generateViewId());
        circleObjects.add(imageCircle.getId());

        if (player == 1) imageCircle.setImageResource(R.drawable.icon_red_circle);
        else if (player == 2) imageCircle.setImageResource(R.drawable.icon_yellow_circle);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintLayout.addView(imageCircle);

        //Get board height --> f(x)=0.750915x+0.000102704 (dp)
        double height1 = 0.750915 * pxToDp(board.getWidth(), getContext());
        double height = height1 - 0.000102704;

        //Get ball size --> f(x)=0.17158x−0.00595806 (dp)
        double ballHeight1 = 0.176574 * height;
        double ballHeight = ballHeight1 - 0.0286586 - 2;

        constraintSet.constrainWidth(imageCircle.getId(), dpToPx((int) ballHeight, getContext()));
        constraintSet.constrainHeight(imageCircle.getId(), dpToPx((int) ballHeight, getContext()));
        constraintSet.connect(imageCircle.getId(),ConstraintSet.TOP,R.id.boardConnect4,ConstraintSet.TOP);
        setupBallColumn(column, constraintSet, imageCircle);
        constraintSet.applyTo(constraintLayout);

        //Get balls separation --> f(x)=0.0243833x-0.00453338
        double ballSeparation1 = 0.02439 * height;
        double ballSeparation = ballSeparation1 - 2.9193197899425*Math.pow(10,-6);

        //Get margin between board and end of picture
        float lowerMargin = (pxToDp(board.getHeight(), getContext()) - ((float) height))/2;

        //Calculate finish position of the animation
        float newY = pxToDp(board.getHeight(), getContext()) - (lowerMargin) - ((float)ballSeparation * 8) - ((float)ballSeparation * (5 - row) * 8) + 1;

        ViewPropertyAnimator animator = imageCircle.animate()
                .translationY(dpToPx((int) newY, getContext()))
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(2000);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private static float pxToDp(double px, Context context) {
        return (float) px / context.getResources().getDisplayMetrics().density;
    }

    private void setupBallColumn(int column, ConstraintSet constraintSet, ImageView imageCircle) {
        switch (column) {
            case 1:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline1Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline2Connect4,ConstraintSet.END);
                break;
            case 2:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline2Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline3Connect4,ConstraintSet.END);
                break;
            case 3:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline3Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline4Connect4,ConstraintSet.END);
                break;
            case 4:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline4Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline5Connect4,ConstraintSet.END);
                break;
            case 5:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline5Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline6Connect4,ConstraintSet.END);
                break;
            case 6:
                constraintSet.connect(imageCircle.getId(),ConstraintSet.START,R.id.guideline6Connect4,ConstraintSet.START);
                constraintSet.connect(imageCircle.getId(),ConstraintSet.END,R.id.guideline7Connect4,ConstraintSet.END);
                break;
        }
    }

    private void resetUIGameFinished() {
        finishGame();
        btnNewGameConnect4.setText(R.string.btnTxtNovaPartida);
        txtInfoGame.setText(R.string.txtGameNotStarted);
        txtInfoPlayer.setText("");
        try {
            for (int circles : circleObjects) {
                ImageView circle = v.findViewById(circles);
                circle.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}
    }

    private void finishGame() {
        GAME_STARTED = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        THREAD_RUNNING = false;
        GAME_STARTED = false;
        connect4ViewModel.finishGameConnect4();
    }

    private void howToPlayDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_how_to_play, null);
        ImageView image = view.findViewById(R.id.imgHowToPlay);
        image.setImageResource(R.drawable.board_connect_4);
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_connect4)
                .setMessage(R.string.txtHelpConnect4)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}