package com.letv.autoapk.ui.player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.widgets.CircleImageView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayCommentAdapter extends BaseAdapter implements OnClickListener {
	public static final int TYPE_COMMENT = 0;
	public static final int TYPE_REPLAY_COMMENT = 1;
	public static final int TYPE_MAX_COUNT = TYPE_REPLAY_COMMENT + 1;
	private Context context;
	private PlayDetailFragment fragment;
	private List<PlayCommentInfo> playCommentInfos;
	private Handler handler;

	PlayCommentAdapter(Context ctx, List<PlayCommentInfo> infos, Handler handler, PlayDetailFragment fragment) {
		this.context = ctx;
		this.fragment = fragment;
		this.playCommentInfos = infos;
		this.handler = handler;
	}

	@Override
	public int getItemViewType(int position) {
		return playCommentInfos.get(position % playCommentInfos.size()).getCommentType();
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return playCommentInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return playCommentInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (getItemViewType(position) == 0) {
				convertView = LayoutInflater.from(context).inflate(R.layout.play_detail_commend_firstitem, null);
				CommentHolder commentHolder = new CommentHolder(convertView);
				convertView.setTag(commentHolder);
			} else if (getItemViewType(position) == 1) {
				convertView = LayoutInflater.from(context).inflate(R.layout.play_detail_commend_seconditem, null);
				CommentHolder commentHolder = new CommentHolder(convertView);
				convertView.setTag(commentHolder);
			}
		}
		Object tag = convertView.getTag();
		if (getItemViewType(position) == 0) {
			if (tag instanceof CommentHolder) {
				CommentHolder commentHolder = (CommentHolder) tag;
				commentHolder.loadingData(playCommentInfos.get(position), position);
				if (position == getCount() - 1 || getItemViewType(position + 1) == 1) {
					commentHolder.lineView.setVisibility(View.GONE);
				} else {
					commentHolder.lineView.setVisibility(View.VISIBLE);
				}
			}
		} else if (getItemViewType(position) == 1) {
			if (tag instanceof CommentHolder) {
				CommentHolder commentHolder = (CommentHolder) tag;
				if (position == getCount() - 1) {
					commentHolder.lineView.setVisibility(View.GONE);
					commentHolder.secLineView.setVisibility(View.GONE);
				} else if (position < getCount() - 1 && getItemViewType(position + 1) == 0) {
					commentHolder.lineView.setVisibility(View.VISIBLE);
					commentHolder.secLineView.setVisibility(View.GONE);
				} else if (position < getCount() - 1 && getItemViewType(position + 1) == 1) {
					commentHolder.lineView.setVisibility(View.GONE);
					commentHolder.secLineView.setVisibility(View.VISIBLE);
				}
				commentHolder.loadingData(playCommentInfos.get(position), position);
			}
		}
		return convertView;
	}

	class CommentHolder {
		private CircleImageView circleImageView;
		private TextView nickName;
		private TextView commentContent;
		private TextView commentTime;
		private ImageView replyComment;
		private TextView priseComment;
		private PlayCommentInfo commentInfo;
		private View lineView;
		private View secLineView;

		public CommentHolder(View view) {
			circleImageView = (CircleImageView) view.findViewById(R.id.play_detail_comment_icon);
			nickName = (TextView) view.findViewById(R.id.play_detail_comment_title);
			commentContent = (TextView) view.findViewById(R.id.play_detail_comment_content);
			commentTime = (TextView) view.findViewById(R.id.play_detail_comment_time);
			replyComment = (ImageView) view.findViewById(R.id.play_detail_comment_reply);
			priseComment = (TextView) view.findViewById(R.id.play_detail_comment_prise);
			lineView = view.findViewById(R.id.commend_line);
			secLineView = view.findViewById(R.id.commend_sec_line);
		}

		public void loadingData(PlayCommentInfo info, int position) {
			this.commentInfo = info;
			circleImageView.setDefaultImageResId(R.drawable.play_comment_user);
			circleImageView.setErrorImageResId(R.drawable.play_comment_user);
			circleImageView.setImageUrl(commentInfo.getUser().getUserIcon(), LruImageCache.getImageLoader(context));
			nickName.setText(commentInfo.getUser().getNickName());
			commentContent.setText(initCommentTitle(commentInfo.getCommentContent(), commentInfo.getReplayNickName()));
			// String commenttime = DateUitls
			// .formatDate(DateUitls.getDateFromGreenwichSec(Long.parseLong(commentInfo.getCommentTime())));
			commentTime.setText(commentInfo.getCommentTime());
			priseComment.setText(PlayerAPI.formatCount(commentInfo.getSupportCount()));
			Drawable priseDrawable = context.getResources().getDrawable(R.drawable.play_tv_like_p);
			priseDrawable.setBounds(0, 0, priseDrawable.getMinimumWidth(), priseDrawable.getMinimumHeight());
			Drawable priseGrayDrawable = context.getResources().getDrawable(R.drawable.play_tv_like);
			priseGrayDrawable.setBounds(0, 0, priseDrawable.getMinimumWidth(), priseDrawable.getMinimumHeight());
			if (commentInfo.isHasSupport()) {
				priseComment.setCompoundDrawables(priseDrawable, null, null, null);
			} else {
				priseComment.setCompoundDrawables(priseGrayDrawable, null, null, null);
			}
			replyComment.setOnClickListener(new CommentListenr(position, commentInfo.getCommentId(),
					commentInfo.getUser().getNickName(), commentInfo.getCommentType()));
			priseComment.setTag(commentInfo);
			priseComment.setOnClickListener(PlayCommentAdapter.this);
			// commentContent.setOnClickListener(PlayCommentAdapter.this);
		}
	}

	private CharSequence initCommentTitle(String commentInfo, String replayName) {
		if (replayName != null && !replayName.isEmpty()) {
			String titleBrief = context.getString(R.string.reply) + replayName + ":" + commentInfo;
			String rexgString = replayName;
			SpannableStringBuilder builder = new SpannableStringBuilder(titleBrief);
			Pattern pattern = Pattern.compile(rexgString);
			Matcher matcher = pattern.matcher(titleBrief);
			while (matcher.find()) {
				builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.code1)),
						matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return builder;
		} else {
			return commentInfo;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_detail_comment_prise:
			PlayCommentInfo commentInfo = (PlayCommentInfo) v.getTag();
			if (commentInfo.getCommentId() != null && !commentInfo.getCommentId().isEmpty()) {
				sendPlayPriseRequest(commentInfo, (TextView) v);
			} else {
				Toast.makeText(context, context.getString(R.string.checking_like), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	class CommentListenr implements OnClickListener {
		int listPositon;
		int replyType;
		String replayId;
		String replayName;

		CommentListenr(int pos, String id, String name, int type) {
			listPositon = pos + 1;
			replayId = id;
			replayName = name;
			this.replyType = type;
		}

		@Override
		public void onClick(View view) {
			if (replayId != null && !replayId.isEmpty()) {
				Message msg = new Message();
				msg.what = PlayDetailFragment.SEND_COMMENT_CONTENT;
				Bundle bundle = new Bundle();
				bundle.putString("replayId", replayId);
				bundle.putInt("listPositon", listPositon);
				bundle.putString("replayName", replayName);
				bundle.putInt("replyType", replyType);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} else {
				Toast.makeText(context, context.getString(R.string.checking_comment), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void sendPlayPriseRequest(final PlayCommentInfo playCommentInfo, TextView view) {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(context);
		if (isNoNetwork) {
			return;
		}
		if (playCommentInfo.isHasSupport()) {
			((BaseActivity) context).showToastSafe(R.string.play_prise_repeat_toast, Toast.LENGTH_SHORT);
			return;
		}
		new UiAsyncTask<Boolean>(fragment) {

			@Override
			protected Boolean doBackground() {
				PraiseUtils praiseUtils = new PraiseUtils();
				boolean doSupport = praiseUtils.sendPraiseRequest("3", "1", playCommentInfo.getCommentId(),
						LoginInfoUtil.getUserId(context), MyApplication.getInstance().getTenantId(), context);

				return doSupport;
			}

			protected void post(Boolean result) {

			};
		}.execute();

		try {
			if (!playCommentInfo.isHasSupport()) {
				Drawable priseDrawable = context.getResources().getDrawable(R.drawable.play_tv_like_p);
				priseDrawable.setBounds(0, 0, priseDrawable.getMinimumWidth(), priseDrawable.getMinimumHeight());
				view.setText(PlayerAPI.formatCount(playCommentInfo.getSupportCount() + 1));
				playCommentInfo.setSupportCount(playCommentInfo.getSupportCount() + 1);
				view.setCompoundDrawables(priseDrawable, null, null, null);
				playCommentInfo.setHasSupport(true);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
