package co.voat.android.dialogs;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.voat.android.R;
import co.voat.android.VoatApp;
import co.voat.android.api.CommentBody;
import co.voat.android.api.CommentResponse;
import co.voat.android.api.VoatClient;
import co.voat.android.data.Submission;
import co.voat.android.events.PostedCommentEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Comments on comments on comments
 * Created by Jawn on 6/13/2015.
 */
public class CommentDialog extends AppCompatDialog {

    @InjectView(R.id.comment_hint)
    TextInputLayout commentHint;
    @InjectView(R.id.comment)
    EditText commentText;

    Submission submission;

    @OnClick(R.id.send)
    void onSendClick(View v) {
        String comment = commentText.getText().toString();
        boolean hasError = false;
        if (TextUtils.isEmpty(comment)) {
            commentHint.setError(getContext().getString(R.string.required_field));
            hasError = true;
        }
        if (!hasError) {
            CommentBody body = new CommentBody.Builder(comment)
                    .build();
            VoatClient.instance().postComment(submission.getSubverse(),
                    submission.getId(),
                    body,
                    postCommentResponse);
        }
    }

    private final Callback<CommentResponse> postCommentResponse = new Callback<CommentResponse>() {
        @Override
        public void success(CommentResponse simpleResponse, Response response) {
            if (simpleResponse.success) {
                Toast.makeText(getContext(), R.string.comment_posted, Toast.LENGTH_SHORT)
                        .show();
                VoatApp.bus().post(new PostedCommentEvent(submission));
                dismiss();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error.toString());
            Toast.makeText(getContext(), R.string.comment_error, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    public CommentDialog(Context context, Submission submission) {
        super(context);
        setContentView(R.layout.dialog_comment);
        ButterKnife.inject(this);
        this.submission = submission;
    }
}
