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


// Class that defines the fragment of the game Connect 4.
public class Connect4 extends Fragment {

    // Definition of status variables
    private static boolean GAME_STARTED = false;
    private static boolean PLAYERS_READY = false;
    private static boolean YOUR_TURN = false;
    private static boolean THREAD_RUNNING = false;
    private static boolean GAME_FINISHED = false;

    private View v;

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private Connect4ViewModel connect4ViewModel;

    // Definition of the variables used by the elements of the layout.
    private ConstraintLayout constraintLayout;
    private TextView txtInfoPlayer, txtInfoGame;
    private ImageView board;
    private Button btnColumn1, btnColumn2, btnColumn3, btnColumn4, btnColumn5, btnColumn6, btnNewGameConnect4;

    // Definition of variables that handle some states (some of them caused because receiving doubled responses, so to control the flow)
    private int player = 0, init = 0, init1 = 0, init2 = 0, update = 0, flag = 0, flag2 = 0, flagClicked = 0;

    // ArrayList to store the ID's of the pieces generated to play the game. Used when having to clear the board.
    private ArrayList<Integer> circleObjects = new ArrayList<>();


    // Fragments require an empty constructor.
    public Connect4() { }

    // Method that creates the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment, defining all the elements of the layout and calling important methods to handle status.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_connect4, container, false);

        // Creation of the ViewModel objects.
        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        connect4ViewModel = new ViewModelProvider(Login.getContext()).get(Connect4ViewModel.class);

        // Definition of the elements of the activity, set some initial values, and call to methods to perform actions when needed.
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

        // Start thread that checks periodically the status of the game.
        THREAD_RUNNING = true;
        startThreadGame();

        // Calls method to check if the robot is connected.
        checkRobotConnection();

        setupRequestResponseObserver();
        setHasOptionsMenu(true);
        return v;
    }

    // When fragment is destroyed, resets the liveData variables.
    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionsViewModel.resetLiveData();
        connect4ViewModel.resetLiveData();
        robotConnectionViewModel.resetLiveData();
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.getString("response").equals("robot-connection-failed")) dialogWarningRobotNotConnected();
                else {
                    setupPermissionsObserver();
                    permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "connect4");
                }
            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

    // Method to check if the robot is connected. All the use of flags is due to multiple responses that affected the flow of the program...
    /*private void checkRobotConnection() {
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
    }*/

    // Method that shows a dialog if the robot is not connected.
    private void dialogWarningRobotNotConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.txtRobotNotConnected)
                .setMessage(R.string.txtCheckRobotConnection)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Setups the observer that will receive all the responses of the requests done when playing, by using the Connect4ViewModel.
    private void setupRequestResponseObserver() {
        connect4ViewModel.connect4RequestResponse();
        connect4ViewModel.getConnect4RequestResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);

                // Handle when game starts.
                if (responseObject.getString("response").equals("connect4-start-success")) {
                    GAME_STARTED = true;
                    player = responseObject.getInt("player");
                    if (player == 1) txtInfoPlayer.setText(getResources().getString(R.string.txtVermelles));
                    else txtInfoPlayer.setText(getResources().getString(R.string.txtGrogues));
                    btnNewGameConnect4.setText(R.string.txtEndGame);
                    txtInfoGame.setText("");
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameStarted, Toast.LENGTH_SHORT).show();
                }

                // Handle when game is full.
                else if (responseObject.getString("response").equals("game-is-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsFull, Toast.LENGTH_SHORT).show();

                // Handle when player 1 has started a new game, but another player needs to click start new game.
                else if (responseObject.getString("response").equals("waiting-for-player") && GAME_STARTED) {
                    txtInfoGame.setText(getResources().getString(R.string.txtWaitingForPlayer));
                    PLAYERS_READY = false;
                }

                // Handle when game starts because there are two players in the game.
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

                // Handle when there has been a status request, to update the game status in each player screen.
                else if (responseObject.getString("response").equals("no-winner") && GAME_STARTED)  {
                    if ((responseObject.getInt("player") != player)) {
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

                // Handle when there is a winner in the current game.
                else if (responseObject.getString("response").equals("winner-1")) {
                    GAME_STARTED = false;
                    if (player == 1) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGameConnect4.setText(R.string.btnTxtNewGame);
                    GAME_FINISHED = true;
                }

                // Handle when there is a winner in the current game.
                else if (responseObject.getString("response").equals("winner-2")) {
                    GAME_STARTED = false;
                    if (player == 2) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGameConnect4.setText(R.string.btnTxtNewGame);
                    GAME_FINISHED = true;
                }

                // Handle when game is over, due to external events.
                else if (responseObject.getString("response").equals("game-is-over") && GAME_STARTED) {
                    resetUIGameFinished();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_LONG).show();
                }

                // Handle when board is full, so no movements are left.
                else if (responseObject.getString("response").equals("board-is-full") && GAME_STARTED) {
                    resetUIGameFinished();
                    connect4ViewModel.finishGameConnect4();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtBoardIsFull, Toast.LENGTH_LONG).show();
                }

                // Handle when the movement that a player has done, is successful.
                else if (responseObject.getString("response").equals("connect4-position-success")) {
                    onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), player);
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtWaitRobot, Toast.LENGTH_LONG).show();
                }

                // Handle when the column selected is full.
                else if (responseObject.getString("response").equals("connect4-position-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtColFull, Toast.LENGTH_LONG).show();

            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

    // Setups the observer that will receive all the responses of the requests done when checking user permissions.
    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) {
                        if (flagClicked == 1)
                            connect4ViewModel.startNewGameConnect4(Login.getIpAddress());
                    } else {
                        if (init != 0) Toast.makeText(getContext(), R.string.txtPermissionsPlayConnect4, Toast.LENGTH_SHORT).show();
                    } init++;
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
    }

    // Method to handle when button to start or finish game is pressed.
    private void startFinishGame() {
        flagClicked = 1;
        if (!GAME_STARTED) {
            flag = 1;
            robotConnectionViewModel.checkRobotConnection();
            resetUIGameFinished();
        } else {
            GAME_FINISHED = true;
            btnNewGameConnect4.setText(R.string.btnTxtNewGame);
            txtInfoGame.setText(R.string.txtGameNotStarted);
            txtInfoPlayer.setText("");
            GAME_STARTED = false;
            connect4ViewModel.finishGameConnect4();
            Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_SHORT).show();
        }
    }

    // Thread that checks periodically the status of the game. If game is finished, clears the board.
    private void startThreadGame() {
        new Thread(()-> {
            int i = 0;
            while (THREAD_RUNNING) {
                while (!GAME_STARTED) {
                    if (!THREAD_RUNNING) break;
                    if (GAME_FINISHED && i == 5) {
                        //GAME_STARTED = false;
                        connect4ViewModel.finishGameConnect4();
                        GAME_FINISHED = false;
                    }
                    // Counter to 5 seconds
                    if (i == 5) i = 0;
                    else i++;
                    try { TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }
                while (GAME_STARTED) {
                    if (!THREAD_RUNNING) break;
                    connect4ViewModel.connect4CheckStatus();
                    try { TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Sends a new movement, when player has selected a column.
    private void connect4Movement(int column) {
        if (GAME_STARTED) {
            if (PLAYERS_READY) {
                if (YOUR_TURN) connect4ViewModel.connect4Position(player, column);
                else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtNotYourTurn, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtCantPlayWaitingOtherPlayer, Toast.LENGTH_SHORT).show();
        } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtFirstStartNewGame, Toast.LENGTH_SHORT).show();
    }

    // Method to handle when a new piece has to be added to the board. Each time, a new piece element is created, and adapted to the screen size.
    private void onNewMovement(int row, int column, int player) {
        // Creates a new piece element
        ImageView imageCircle = new ImageView(getContext());
        imageCircle.setId(View.generateViewId());
        circleObjects.add(imageCircle.getId());

        if (player == 1) imageCircle.setImageResource(R.drawable.connect_4_red);
        else if (player == 2) imageCircle.setImageResource(R.drawable.connect_4_yellow);

        // Sets the constraints of this new element
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintLayout.addView(imageCircle);

        // As the app can run in multiple screen sizes, we have to calculate different aspects to adapt board size, piece size, separations...
        // This has been done looking for the function that defines each case. Then, an animation is used to simulate a piece falling, so the new
        // position of the piece has to be calculed.

        // Get board height --> f(x)=0.750915x+0.000102704 (dp)
        double height1 = 0.750915 * pxToDp(board.getWidth(), getContext());
        double height = height1 - 0.000102704;

        // Get ball size --> f(x)=0.17158x−0.00595806 (dp)
        double ballHeight1 = 0.176574 * height;
        double ballHeight = ballHeight1 - 0.0286586 - 2;

        constraintSet.constrainWidth(imageCircle.getId(), dpToPx((int) ballHeight, getContext()));
        constraintSet.constrainHeight(imageCircle.getId(), dpToPx((int) ballHeight, getContext()));
        constraintSet.connect(imageCircle.getId(),ConstraintSet.TOP,R.id.boardConnect4,ConstraintSet.TOP);
        setupBallColumn(column, constraintSet, imageCircle);
        constraintSet.applyTo(constraintLayout);

        // Get balls separation --> f(x)=0.0243833x-0.00453338
        double ballSeparation1 = 0.02439 * height;
        double ballSeparation = ballSeparation1 - 2.9193197899425*Math.pow(10,-6);

        // Get margin between board and end of picture
        float lowerMargin = (pxToDp(board.getHeight(), getContext()) - ((float) height))/2;

        // Calculate finish position of the animation
        float newY = pxToDp(board.getHeight(), getContext()) - (lowerMargin) - ((float)ballSeparation * 8) - ((float)ballSeparation * (5 - row) * 8) + 1;

        // Setup the animation
        ViewPropertyAnimator animator = imageCircle.animate()
                .translationY(dpToPx((int) newY, getContext()))
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(2000);

        // Methods of the animation that have to be defined but not used in this case.
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) { }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        animator.start();
    }

    // Method to pass from dp to Px (image size units)
    private static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Method to pass from px to dp (image size units)
    private static float pxToDp(double px, Context context) {
        return (float) px / context.getResources().getDisplayMetrics().density;
    }

    // Method used to set the constraints. It defines where has the ball to be placed according to the selected column.
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

    // Resets the UI when a game is finished.
    private void resetUIGameFinished() {
        GAME_STARTED = false;
        btnNewGameConnect4.setText(R.string.btnTxtNewGame);
        txtInfoGame.setText(R.string.txtGameNotStarted);
        txtInfoPlayer.setText("");
        try {
            for (int circles : circleObjects) {
                ImageView circle = v.findViewById(circles);
                circle.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {}
    }

    // When fragment is destroyed, it stops the thread and the game.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        THREAD_RUNNING = false;
        GAME_STARTED = false;
        connect4ViewModel.finishGameConnect4();
    }

    // Opens a dialog that shows an image of the board.
    private void howToPlayDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_how_to_play, null);
        ImageView image = view.findViewById(R.id.imgHowToPlay);
        image.setImageResource(R.drawable.board_connect_4);
        dialog.setContentView(view);
        dialog.show();
    }

    // Changes the help menu item visibility to true.
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    // When the help menu item is clicked, opens help dialog.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    // Opens a dialog that explains how to play to Connect 4.
    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_connect4)
                .setMessage(R.string.txtHelpConnect4)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}