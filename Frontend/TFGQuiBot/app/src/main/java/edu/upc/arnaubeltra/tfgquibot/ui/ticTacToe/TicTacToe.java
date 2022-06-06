package edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class TicTacToe extends Fragment {

    private static boolean GAME_STARTED = false;
    private static boolean PLAYERS_READY = false;
    private static boolean YOUR_TURN = false;
    private static boolean IS_AUTHORIZED = false;
    private static boolean ROBOT_READY = false;
    private static boolean THREAD_RUNNING = false;
    private static boolean GAME_FINISHED = false;

    private TextView txtInfoPlayer, txtInfoGame;
    private Button btnTicTacToe1, btnTicTacToe2, btnTicTacToe3, btnTicTacToe4, btnTicTacToe5, btnTicTacToe6, btnTicTacToe7, btnTicTacToe8, btnTicTacToe9, btnNewGame;

    private TicTacToeViewModel ticTacToeViewModel;
    private PermissionsViewModel permissionsViewModel;
    private RobotConnectionViewModel robotConnectionViewModel;

    private int player = 0;
    private int init = 0, init2 = 0;
    private Boolean robotConnected = false;
    private int flag = 0, flag2 = 0;
    private int flagStarted = 0;

    // Required empty public constructor
    public TicTacToe() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tic_tac_toe, container, false);

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        ticTacToeViewModel = new ViewModelProvider(Login.getContext()).get(TicTacToeViewModel.class);

        btnTicTacToe1 = v.findViewById(R.id.btnTicTacToe1);
        btnTicTacToe1.setOnClickListener(view -> ticTacToeMovement(0, 0));
        btnTicTacToe2 = v.findViewById(R.id.btnTicTacToe2);
        btnTicTacToe2.setOnClickListener(view -> ticTacToeMovement(0, 1));
        btnTicTacToe3 = v.findViewById(R.id.btnTicTacToe3);
        btnTicTacToe3.setOnClickListener(view -> ticTacToeMovement(0, 2));
        btnTicTacToe4 = v.findViewById(R.id.btnTicTacToe4);
        btnTicTacToe4.setOnClickListener(view -> ticTacToeMovement(1, 0));
        btnTicTacToe5 = v.findViewById(R.id.btnTicTacToe5);
        btnTicTacToe5.setOnClickListener(view -> ticTacToeMovement(1, 1));
        btnTicTacToe6 = v.findViewById(R.id.btnTicTacToe6);
        btnTicTacToe6.setOnClickListener(view -> ticTacToeMovement(1, 2));
        btnTicTacToe7 = v.findViewById(R.id.btnTicTacToe7);
        btnTicTacToe7.setOnClickListener(view -> ticTacToeMovement(2, 0));
        btnTicTacToe8 = v.findViewById(R.id.btnTicTacToe8);
        btnTicTacToe8.setOnClickListener(view -> ticTacToeMovement(2, 1));
        btnTicTacToe9 = v.findViewById(R.id.btnTicTacToe9);
        btnTicTacToe9.setOnClickListener(view -> ticTacToeMovement(2, 2));

        btnNewGame = v.findViewById(R.id.btnNewGame);
        btnNewGame.setText(R.string.btnTxtNewGame);
        btnNewGame.setOnClickListener(view -> startFinishGame());
        v.findViewById(R.id.btnHowToPlay).setOnClickListener(view -> howToPlayDialog());

        txtInfoGame = v.findViewById(R.id.txtInfoGame);
        txtInfoPlayer = v.findViewById(R.id.txtInfoPlayer);

        THREAD_RUNNING = true;
        startThreadGame();

        checkRobotConnection();
        setupRequestResponseObserver();
        //onGetUserPermissions();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionsViewModel.resetLiveData();
        ticTacToeViewModel.resetLiveData();
    }

    @Override
    public void onStart() {
        super.onStart();
        flagStarted = 1;
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
                            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "tic_tac_toe"); flag2++;
                        } else if ((flag2 == 2) && init2 != 0)
                            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "tic_tac_toe");
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
        ticTacToeViewModel.ticTacToeRequestResponse();
        ticTacToeViewModel.getTicTacToeRequestResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);

                if (responseObject.getString("response").equals("tic-tac-toe-start-success")) {
                    GAME_STARTED = true;
                    player = responseObject.getInt("player");
                    if (player == 1) txtInfoPlayer.setText(getResources().getString(R.string.txtYouPlayWithX));
                    else txtInfoPlayer.setText(getResources().getString(R.string.txtYouPlayWithO));
                    btnNewGame.setText(R.string.txtEndGame);
                    txtInfoGame.setText("");
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameStarted, Toast.LENGTH_SHORT).show();
                }

                else if (responseObject.getString("response").equals("game-is-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsFull, Toast.LENGTH_SHORT).show();

                else if (responseObject.getString("response").equals("waiting-for-player") && GAME_STARTED) {
                    txtInfoGame.setText(getResources().getString(R.string.txtWaitingForPlayer));
                    PLAYERS_READY = false;
                }

                else if (responseObject.getString("response").equals("tic-tac-toe-init") && GAME_STARTED) {
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
                    if (responseObject.getInt("player") != player) {
                        onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), responseObject.getInt("player"));
                        txtInfoGame.setText(getResources().getString(R.string.txtYourTurn));
                        YOUR_TURN = true;
                    } else {
                        txtInfoGame.setText(getResources().getString(R.string.txtHisTurn));
                        YOUR_TURN = false;
                    }
                }

                else if (responseObject.getString("response").equals("winner-1")) {
                    GAME_STARTED = false;
                    if (player == 1) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGame.setText(R.string.btnTxtNewGame);
                    GAME_FINISHED = true;
                    //finishGame();
                }

                else if (responseObject.getString("response").equals("winner-2")) {
                    GAME_STARTED = false;
                    if (player == 2) txtInfoGame.setText(R.string.txtYouWon);
                    else txtInfoGame.setText(R.string.txtYouLost);
                    btnNewGame.setText(R.string.btnTxtNewGame);
                    GAME_FINISHED = true;
                    //finishGame();
                }

                else if (responseObject.getString("response").equals("game-is-over") && GAME_STARTED) {
                    resetUIGameFinished();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("board-is-full") && GAME_STARTED) {
                    resetUIGameFinished();
                    ticTacToeViewModel.finishGameTicTacToe();
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtBoardIsFull, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("tic-tac-toe-position-success")) {
                    onNewMovement(responseObject.getInt("x"), responseObject.getInt("y"), player);
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtWaitRobot, Toast.LENGTH_LONG).show();
                }

                else if (responseObject.getString("response").equals("tic-tac-toe-position-full"))
                    Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtPositionFull, Toast.LENGTH_LONG).show();

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
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match"))
                        ticTacToeViewModel.startNewGameTicTacToe(Login.getIpAddress());
                    else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtPermissionsPlayTicTacToe, Toast.LENGTH_SHORT).show();
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
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) {
                        if (flagStarted != 1)
                            ticTacToeViewModel.startNewGameTicTacToe(Login.getIpAddress());
                        flagStarted = 0;
                    }
                    else Toast.makeText(getContext(), R.string.txtPermissionsPlayTicTacToe, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void startFinishGame() {
        if (!GAME_STARTED) {
            //setupPermissionsObserver();
            //permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "tic_tac_toe");
            flag = 1;
            robotConnectionViewModel.checkRobotConnection();
        } else {
            GAME_FINISHED = true;
            btnNewGame.setText(R.string.btnTxtNewGame);
            txtInfoGame.setText(R.string.txtGameNotStarted);
            txtInfoPlayer.setText("");
            finishGame();
            ticTacToeViewModel.finishGameTicTacToe();
            Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtGameIsOver, Toast.LENGTH_SHORT).show();
        }
    }

    private void startThreadGame() {
        new Thread(()-> {
            int i = 0;
            while (THREAD_RUNNING) {
                while (!GAME_STARTED) {
                    Log.d("TAG", "GAME NOT STARTED: " + GAME_FINISHED + " " + i);
                    if (!THREAD_RUNNING) break;
                    //permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "tic_tac_toe");
                    if (GAME_FINISHED && i == 5) {
                        Log.d("TAG", "GAME FINISHED TRUE");
                        finishGame();
                        ticTacToeViewModel.finishGameTicTacToe();
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
                    ticTacToeViewModel.ticTacToeCheckStatus();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void ticTacToeMovement(int x, int y) {
        if (GAME_STARTED) {
            if (PLAYERS_READY) {
                if (YOUR_TURN) ticTacToeViewModel.ticTacToePosition(player, x, y);
                else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtNotYourTurn, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtCantPlayWaitingOtherPlayer, Toast.LENGTH_SHORT).show();
        } else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtFirstStartNewGame, Toast.LENGTH_SHORT).show();
    }

    private void onNewMovement(int x, int y, int player) {
        String sign;
        if (player == 1) sign = "X" ;
        else sign = "O";
        switch (x) {
            case 0:
                switch (y) {
                    case 0:
                        btnTicTacToe1.setText(sign);
                        break;
                    case 1:
                        btnTicTacToe2.setText(sign);
                        break;
                    case 2:
                        btnTicTacToe3.setText(sign);
                        break;
                } break;
            case 1:
                switch (y) {
                    case 0:
                        btnTicTacToe4.setText(sign);
                        break;
                    case 1:
                        btnTicTacToe5.setText(sign);
                        break;
                    case 2:
                        btnTicTacToe6.setText(sign);
                        break;
                } break;
            case 2:
                switch (y) {
                    case 0:
                        btnTicTacToe7.setText(sign);
                        break;
                    case 1:
                        btnTicTacToe8.setText(sign);
                        break;
                    case 2:
                        btnTicTacToe9.setText(sign);
                        break;
                } break;
        }
    }

    private void finishGame() {
        //ticTacToeViewModel.finishGameTicTacToe();
        GAME_STARTED = false;
        btnTicTacToe1.setText("");
        btnTicTacToe2.setText("");
        btnTicTacToe3.setText("");
        btnTicTacToe4.setText("");
        btnTicTacToe5.setText("");
        btnTicTacToe6.setText("");
        btnTicTacToe7.setText("");
        btnTicTacToe8.setText("");
        btnTicTacToe9.setText("");
    }

    private void resetUIGameFinished() {
        finishGame();
        btnNewGame.setText(R.string.btnTxtNewGame);
        txtInfoGame.setText(R.string.txtGameNotStarted);
        txtInfoPlayer.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        THREAD_RUNNING = false;
        GAME_STARTED = false;
        ticTacToeViewModel.finishGameTicTacToe();
    }

    private void howToPlayDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_how_to_play, null);
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
        builder.setTitle(R.string.menu_tic_tac_toe)
                .setMessage(R.string.txtHelpTicTacToe)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}