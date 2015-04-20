package net.i2p.android.i2ptunnel.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import net.i2p.android.i2ptunnel.TunnelDetailActivity;
import net.i2p.android.i2ptunnel.TunnelDetailFragment;
import net.i2p.android.router.R;

public class EditTunnelActivity extends ActionBarActivity {
    private int mTunnelId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        // Set the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mTunnelId = getIntent().getIntExtra(TunnelDetailFragment.TUNNEL_ID, 0);
            Fragment editFrag = GeneralTunnelPreferenceFragment.newInstance(mTunnelId);
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, editFrag).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            Intent intent = new Intent(this, TunnelDetailActivity.class);
            intent.putExtra(TunnelDetailFragment.TUNNEL_ID, mTunnelId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
