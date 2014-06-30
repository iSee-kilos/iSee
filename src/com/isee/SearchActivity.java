package com.isee;

import com.isee.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SearchActivity extends FragmentActivity
{

    private int selection = 0;
	private int oldSelection = -1;

	private String[] names = null;
	private String[] classes = null;
	
	private ActionBarDrawerToggle drawerToggle = null;
	private DrawerLayout drawer = null;
	private ListView navList = null;
	
	private static final String OPENED_KEY = "OPENED_KEY";
	private SharedPreferences prefs = null;
	private Boolean opened = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		names = getResources().getStringArray(R.array.nav_names);
		classes = getResources().getStringArray(R.array.nav_classes);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1, names);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		navList = (ListView) findViewById(R.id.drawer);
		navList.setAdapter(adapter);
		drawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.open, R.string.close)
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
				updateContent();
				invalidateOptionsMenu();
				if(opened != null && opened == false)
				{
					opened = true;
					if(prefs != null)
					{
						Editor editor = prefs.edit();
						editor.putBoolean(OPENED_KEY, true);
						editor.apply();
					}
				}
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}
		};
		drawer.setDrawerListener(drawerToggle);

		navList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int pos, long id)
			{
				selection = pos;
				drawer.closeDrawer(navList);
			}
		});

		updateContent();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				prefs = getPreferences(MODE_PRIVATE);
				opened = prefs.getBoolean(OPENED_KEY, false);
				if(opened == false)
				{
					drawer.openDrawer(navList);
				}
			}
			
		}).start();

    }
    
    @Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if(drawer != null && navList != null)
		{
			MenuItem item = menu.findItem(R.id.add);
			if(item != null)
			{
				item.setVisible(!drawer.isDrawerOpen(navList));
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void updateContent()
	{
		if (oldSelection != selection)
		{
			getActionBar().setTitle(names[selection]);
			Toast.makeText(getApplicationContext(), names[selection], Toast.LENGTH_LONG).show();
			FragmentTransaction tx = getSupportFragmentManager()
					.beginTransaction();
			tx.replace(R.id.main,
					Fragment.instantiate(SearchActivity.this, classes[selection]));
			tx.commitAllowingStateLoss();
			oldSelection = selection;
		}
	}	
	
	/**
	 * Creates a dialog and shows it
	 * 
	 * @param exception
	 *            The exception to show in the dialog
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(Exception exception, String title) {
		Throwable ex = exception;
		if(exception.getCause() != null){
			ex = exception.getCause();
		}
		createAndShowDialog(ex.getMessage(), title);
	}

	/**
	 * Creates a dialog and shows it
	 * 
	 * @param message
	 *            The dialog message
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
}