package org.sketcher;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {
	public AboutDialog(Context context) {
		super(context);

		setTitle(R.string.about_title);
		setContentView(R.layout.about);

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
