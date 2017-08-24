/*
 * Copyright (C) 2011-2012 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This file is part of AdAway.
 * 
 * AdAway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdAway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdAway.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.adaway.ui.tcpdump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.adaway.R;
import org.adaway.util.Constants;
import org.adaway.util.Log;
import org.adaway.util.TcpdumpUtils;
import org.sufficientlysecure.rootcommands.Shell;

import java.io.IOException;

/**
 * This class is a fragment to start/stop tcpdump tool and read/delete its log.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
public class TcpdumpFragment extends Fragment {
    /** The root shell to start and stop tcpdump. */
    private Shell mRootShell;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.tcpdump_fragment, container, false);
        // Get activity
        final Activity activity = this.getActivity();
        // Create root shell
        try {
            this.mRootShell = Shell.startRootShell();
        } catch (Exception exception) {
            Log.e(Constants.TAG, "Unable to create root shell for tcpdump.", exception);
        }
        /*
         * Configure view.
         */
        // Get tcpdump toggle button
        final ToggleButton tcpdumpToggleButton = view.findViewById(R.id.tcpdump_fragment_toggle_button);
        // Set tcpdump toggle button checked state according tcpdump running state
        tcpdumpToggleButton.setChecked(TcpdumpUtils.isTcpdumpRunning(this.mRootShell));
        // Set tcpdump toggle button checked listener to start/stop tcpdump
        tcpdumpToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                // Check shell
                if (TcpdumpFragment.this.mRootShell == null) {
                    return;
                }
                // Check button checked state
                if (checked) {
                    // Start tcpdump
                    if (!TcpdumpUtils.startTcpdump(activity, TcpdumpFragment.this.mRootShell)) {
                        // If tcpdump start failed, uncheck button
                        tcpdumpToggleButton.setChecked(false);
                    }
                } else {
                    // Stop tcpdump
                    TcpdumpUtils.stopTcpdump(activity, TcpdumpFragment.this.mRootShell);
                }
            }
        });
        // Get open button
        Button openButton = view.findViewById(R.id.tcpdump_fragment_open_button);
        // Set open button on click listener to start tcpdump log activity
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpdumpFragment.this.startActivity(new Intent(activity, TcpdumpLogActivity.class));
            }
        });
        // Get delete button
        Button deleteButton = view.findViewById(R.id.tcpdump_fragment_delete_button);
        // Set delete button on click listener to delete tcpdump log
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpdumpUtils.deleteLog(activity);
            }
        });
        // Return created view
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Check if shell is initialized
        if (this.mRootShell != null) {
            try {
                // Close shell
                this.mRootShell.close();
                this.mRootShell = null;
            } catch (IOException exception) {
                Log.e(Constants.TAG, "Unable to close tcpdump shell.", exception);
            }
        }
    }
}
