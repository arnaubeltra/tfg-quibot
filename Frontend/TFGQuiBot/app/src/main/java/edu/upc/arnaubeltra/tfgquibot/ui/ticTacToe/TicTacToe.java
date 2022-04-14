package edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;

public class TicTacToe extends Fragment {

    private static final boolean GAME_STARTED = false;

    private TextView txtInfoPlayer, txtInfoGame;
    private Button btnTicTacToe1, btnTicTacToe2, btnTicTacToe3, btnTicTacToe4, btnTicTacToe5, btnTicTacToe6, btnTicTacToe7, btnTicTacToe8, btnTicTacToe9, btnNewGame;

    // Required empty public constructor
    public TicTacToe() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_tic_tac_toe, container, false);

        btnTicTacToe1 = v.findViewById(R.id.btnTicTacToe1);
        btnTicTacToe1.setOnClickListener(view -> ticTacToeMovement(0, 0));
        btnTicTacToe2 = v.findViewById(R.id.btnTicTacToe2);
        btnTicTacToe2.setOnClickListener(view -> ticTacToeMovement(1, 0));
        btnTicTacToe3 = v.findViewById(R.id.btnTicTacToe3);
        btnTicTacToe3.setOnClickListener(view -> ticTacToeMovement(2, 0));
        btnTicTacToe4 = v.findViewById(R.id.btnTicTacToe4);
        btnTicTacToe4.setOnClickListener(view -> ticTacToeMovement(0, 1));
        btnTicTacToe5 = v.findViewById(R.id.btnTicTacToe5);
        btnTicTacToe5.setOnClickListener(view -> ticTacToeMovement(1, 1));
        btnTicTacToe6 = v.findViewById(R.id.btnTicTacToe6);
        btnTicTacToe6.setOnClickListener(view -> ticTacToeMovement(2, 1));
        btnTicTacToe7 = v.findViewById(R.id.btnTicTacToe7);
        btnTicTacToe7.setOnClickListener(view -> ticTacToeMovement(0, 2));
        btnTicTacToe8 = v.findViewById(R.id.btnTicTacToe8);
        btnTicTacToe8.setOnClickListener(view -> ticTacToeMovement(1, 2));
        btnTicTacToe9 = v.findViewById(R.id.btnTicTacToe9);
        btnTicTacToe9.setOnClickListener(view -> ticTacToeMovement(2, 2));

        btnNewGame = v.findViewById(R.id.btnNewGame);
        btnNewGame.setText(R.string.btnTxtNovaPartida);
        btnNewGame.setOnClickListener(view -> startGame());
        v.findViewById(R.id.btnHowToPlay).setOnClickListener(view -> howToPlayDialog());

        txtInfoGame = v.findViewById(R.id.txtInfoGame);
        txtInfoPlayer = v.findViewById(R.id.txtInfoPlayer);

        return v;
    }

    private void startGame() {
        if (!GAME_STARTED) {
            btnNewGame.setText(R.string.txtEndGame);
            Toast.makeText(UserNavigation.getInstance(), R.string.txtGameStarted, Toast.LENGTH_SHORT).show();
        } else {
            btnNewGame.setText(R.string.btnTxtNovaPartida);
            txtInfoPlayer.setText(R.string.txtGameNotStarted);
            txtInfoGame.setText("");
            finishGame();
        }
    }

    private void ticTacToeMovement(int x, int y) {
        String player = getPlayer();
        if (!GAME_STARTED) {
            Toast.makeText(UserNavigation.getInstance(), R.string.txtFirstStartNewGame, Toast.LENGTH_SHORT).show();
            txtInfoGame.setText(R.string.txtGameNotStarted);
        } else {
            txtInfoPlayer.setText(player);

        }
    }

    private String getPlayer() {
        //Get player 1 or 2
        return "";
    }

    private String getXorO(String player) {
        if (player.equals("1")) {

        } else {

        }
        return "";
    }

    private void howToPlayDialog() {
    }

    private void finishGame() {
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
}