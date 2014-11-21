package com.imherolddev.dailylogkeeper.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.imherolddev.dailylogkeeper.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by imherolddev
 */
public class HelpDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_help, null);

        TextView help = (TextView) view.findViewById(R.id.tv_help);

        try {

            InputStreamReader input = new InputStreamReader(getResources()
                    .openRawResource(R.raw.help));
            BufferedReader br = new BufferedReader(input);

            String line;

            while ((line = br.readLine()) != null) {

                help.append(line);
                help.append("\n");

            }

            input.close();
            br.close();

        } catch (IOException ioex) {

            // catch

        }

        builder.setTitle(R.string.dialog_help_title);
        builder.setView(view);

        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Empty
            }
        });

        return builder.create();

    }

}
