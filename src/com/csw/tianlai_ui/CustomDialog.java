package com.csw.tianlai_ui;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		
		
		
		
		/**
		 * 弹出升级下载的框
		 * @return
		 */
		public CustomDialog createUpdateDownload() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.dialog_normal_layout_update_2,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			
			
			
			
			// set the confirm button
						if (positiveButtonText != null) {
							((Button) layout.findViewById(R.id.positiveButton))
									.setText(positiveButtonText);
							if (positiveButtonClickListener != null) {
								((Button) layout.findViewById(R.id.positiveButton))
										.setOnClickListener(new View.OnClickListener() {
											public void onClick(View v) {
												positiveButtonClickListener.onClick(dialog,
														DialogInterface.BUTTON_POSITIVE);
											}
										});
							}

							((Button) layout.findViewById(R.id.positiveButton))
									.setOnFocusChangeListener(new OnFocusChangeListener() {

										public void onFocusChange(View v, boolean hasFocus) {
											// TODO Auto-generated method stub
											if (hasFocus == true) {
												((Button) layout
														.findViewById(R.id.positiveButton))
														.setBackgroundColor(0xff8b89ff);
											} else {
												((Button) layout
														.findViewById(R.id.positiveButton))
														.setBackgroundColor(0x33ffffff);
											}
										}
									});
						} else {
							// if no confirm button just set the visibility to GONE
							layout.findViewById(R.id.positiveButton).setVisibility(
									View.GONE);
						}
						// set the cancel button
						
						// set the content message
						if (message != null) {
							((TextView) layout.findViewById(R.id.message)).setText(message);
							
						} 
						if (contentView != null) {
							// if no message set
							// add the contentView to the dialog body
							((LinearLayout) layout.findViewById(R.id.content))
									.removeAllViews();
							((LinearLayout) layout.findViewById(R.id.content)).addView(
									contentView, new LayoutParams(LayoutParams.FILL_PARENT,
											LayoutParams.FILL_PARENT));
						}
			
			
			
			
			dialog.setContentView(layout);
			return dialog;
		}


	
		
		/**
		 * wifi输入密码
		 * @return
		 */
		public CustomDialog createWifiDialog() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.dialog_normal_wifi_layout,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}

				((Button) layout.findViewById(R.id.positiveButton))
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub
								if (hasFocus == true) {
									((Button) layout
											.findViewById(R.id.positiveButton))
											.setBackgroundColor(0xff8b89ff);
								} else {
									((Button) layout
											.findViewById(R.id.positiveButton))
											.setBackgroundColor(0x33ffffff);
								}
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}

				((Button) layout.findViewById(R.id.negativeButton))
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub
								if (hasFocus == true) {
									((Button) layout
											.findViewById(R.id.negativeButton))
											.setBackgroundColor(0xff8b89ff);
								} else {
									((Button) layout
											.findViewById(R.id.negativeButton))
											.setBackgroundColor(0x33ffffff);
								}
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
				
			} 
			if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
		
		/**
		 * 弹出手机下载的框
		 * @return
		 */
		public CustomDialog createMobileDownload() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.dialog_normal_layout_mobile,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			
			dialog.setContentView(layout);
			return dialog;
		}

		/**
		 * 确认是否需要关机
		 * @return
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.dialog_normal_layout,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}

				((Button) layout.findViewById(R.id.positiveButton))
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub
								if (hasFocus == true) {
									((Button) layout
											.findViewById(R.id.positiveButton))
											.setBackgroundColor(0xff8b89ff);
								} else {
									((Button) layout
											.findViewById(R.id.positiveButton))
											.setBackgroundColor(0x33ffffff);
								}
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}

				((Button) layout.findViewById(R.id.negativeButton))
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub
								if (hasFocus == true) {
									((Button) layout
											.findViewById(R.id.negativeButton))
											.setBackgroundColor(0xff8b89ff);
								} else {
									((Button) layout
											.findViewById(R.id.negativeButton))
											.setBackgroundColor(0x33ffffff);
								}
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
		
		
	}
	
	
	
}
