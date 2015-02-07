package com.gdg.mmust.studyjams.inviter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Members extends Activity implements OnClickListener {
	EditText etName, etNumber;
	Button btSaveMember;
	ListView lvMembers;
	Utils utils;
	List<MembersList> lMembers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.members);

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		utils = new Utils(Members.this);
		etName = (EditText) findViewById(R.id.etName);
		etNumber = (EditText) findViewById(R.id.etNumber);
		btSaveMember = (Button) findViewById(R.id.btAddMember);
		btSaveMember.setOnClickListener(this);
		lvMembers = (ListView) findViewById(R.id.lvMembers);
		updateMembersList();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		saveMember(etName, etNumber);
	}

	private void saveMember(EditText etName, EditText etNumber) {
		// TODO Auto-generated method stub
		String name = etName.getText().toString();
		String number = etNumber.getText().toString();

		if (!name.equals("") && !number.equals("")) {
			ContentValues values = new ContentValues();
			values.put("person_name", name);
			values.put("person_number", number);
			utils.insertMember(values);
			updateMembersList();
		} else {
			if (name.equals("")) {
				etName.setError("Enter name");
			}
			if (number.equals("")) {
				etName.setError("Enter number");
			}
		}
	}

	private void updateMembersList() {
		// TODO Auto-generated method stub
		lMembers = new ArrayList<Members.MembersList>();
		Cursor c = utils.getCursor();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			lMembers.add(new MembersList(c.getString(c.getColumnIndex("person_name")), c.getString(c.getColumnIndex("person_number"))));
		}
		
		lvMembers.setAdapter(new MembersAdapter());
	}

	class MembersAdapter extends ArrayAdapter<MembersList> {

		public MembersAdapter() {
			super(Members.this, R.layout.members_ui, lMembers);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View memberView = convertView;
			if (memberView == null) {
				memberView = getLayoutInflater().inflate(R.layout.members_ui,
						parent, false);
			}

			TextView tvName = (TextView) memberView.findViewById(R.id.tvName);
			TextView tvNumber = (TextView) memberView
					.findViewById(R.id.tvPhone);

			MembersList membersList = lMembers.get(position);

			tvName.setText(membersList.getMemberName());
			tvNumber.setText(membersList.getMemberNumber());
			return memberView;
		}
	}

	class MembersList {
		String memberName, memberNumber;

		public MembersList(String memberName, String memberNumber) {
			super();
			this.memberName = memberName;
			this.memberNumber = memberNumber;
		}

		public String getMemberName() {
			return memberName;
		}

		public String getMemberNumber() {
			return memberNumber;
		}

	}
}
