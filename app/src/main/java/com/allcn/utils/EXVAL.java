package com.allcn.utils;

import com.allcn.activities.HotReAct;
import com.allcn.activities.KindMovieAct;
import com.allcn.activities.LiveAct;
import com.allcn.activities.PlayBackAct;
import com.allcn.activities.PlayBackMainAct;
import com.allcn.activities.SEDMMainAct;
import com.allcn.activities.SetAct;
import com.allcn.activities.TopicsMainAct;
import com.allcn.activities.YYSJAct;
import com.mast.lib.utils.VAL;

public class EXVAL extends VAL {
    public static final int HOME_ICON_NUM = 9;
    public static final String HOME_INDEX = "HOME_INDEX";
    public static final String KIND_INDEX = "KIND_INDEX";
    public static final String KIND_NAME = "KIND_NAME";
    public static final String CKIND_INDEX = "CKIND_INDEX";
    public static final int CHANGE_HONE_INFO_DURATION = 200;
    public static final int CHANGE_HONE_ICON_DURATION = 50;
    public static final float KIND_MOVIE_ITEM_SCALE = 1.1f;
    public static final float SEDM_MOVIE_ITEM_SCALE = 1.2f;
    public static final int KIND_LIST_SHOW_MIN_LEVEL = 10;
    public static final int KIND_LIST_SHOW_MID_LEVEL = 8;
    public static final int KIND_LIST_SHOW_MAX_LEVEL = 6;
    public static final int LEFT_RELATE_ADAPTER = 0;
    public static final int TOP_RELATE_ADAPTER = 1;
    public static final int RIGHT_RELATE_ADAPTER = 2;
    public static final int BOTTOM_RELATE_ADAPTER = 3;
    public static final int COLLECTION_NUM_IN_LINE = 15;
    public static final int SEDM_COLLECTION_NUM_IN_PAGE = 50;
    public static final int SEDM_COLLECTION_NUM_IN_LINE = 10;
    public static final int TOPICS_CKIND_NUM_IN_PAGE = 10;
    public static final int IS_REVERSE = 1;
    public static final int NO_REVERSE = 2;
    public static final String TITLE_TEXT = "TITLE_TEXT";
    public static final String REVERSE_KEY = "REVERSE_KEY";
    public static final long MARQUEE_DURATION = 20 * 60 * 1000; // 20min
    public static final String TIPS_IMG_URL = "%s/list/image/live/list.txt";
    public static final int NUM_IN_COL_LIVE = 7;
    public static final String R_DB_NAME = "R";
    public static final String W_DB_NAME = "W";
    public static final String R_JOURNAL_NAME = "R-journal";
    public static final String W_JOURNAL_NAME = "W-journal";
    public static final String FAV_DB_NAME = "FAV";
    public static final String HISTORY_DB_NAME = "HISTORY";
    public static final String OTH_DB_NAME = "OTH";
    public static final String ALL_LIVE_DB_NAME = "ALL_LIVE";
    public static final int SEARCH_NUM_IN_PAGE = 8;
    public static final int SEARCH_NUM_IN_ROW = 4;
    public static final int NUM_IN_PAGE = 10;
    public static final int NUM_IN_PAGE_FAV_HISTORY = 10;
    public static final int NUM_IN_PAGE_MTV = 10;
    public static final int PTYPE_CATEGORY_LIVE = 0;
    public static final String NEW_MEDIA_OK = "NEW_MEDIA_OK";
    public static final String NEED_UPDATE = "NEED_UPDATE";
    public static final String IS_YYSJ = "IS_YYSJ";
    public static final int TYPE_ACTIVITY_MAIN = 0;
    public static final int TYPE_ACTIVITY_OTH = 1;
    public static final int TYPE_ACTIVITY_PLAYBACK = 2;
    public static final int TYPE_ACTIVITY_VIDEO = 3;
    public static final int TYPE_ACTIVITY_MOVIE = 4;
    public static final int TYPE_ACTIVITY_ALL = 5;
    public static final int TYPE_AREA_INDIA = 0;
    public static final int TYPE_AREA_WESTERN = 1;
    public static final int TYPE_AREA_NONE = -1;
    public static final int START_ALLTV_ANIM_DURATION = 50;
    public static final int HOME_LIVE_INDEX = 0;
    public static final int HOME_PLAYBACK_INDEX = 1;
    public static final int HOME_YYSJ_INDEX = 2;
    public static final int HOME_YYXF_INDEX = 3;
    public static final int HOME_SEDM_INDEX = 4;
    public static final int HOME_HOT_RE_INDEX = 5;
    public static final int HOME_LGYY_INDEX = 6;
    public static final int HOME_HOT_TOPICS_INDEX = 7;
    public static final int HOME_SET_MGR_INDEX = 8;
    public static final int RE_MOVIE_NUM = 7;
    public static final String MOVIE_OBJ = "movie_obj";
    public static final String ORDER = "ORDER";
    public static final int PLAY_NOREADY = 7;
    public static final int PLAY_PREPARED = 8;
    public static final int PLAY_OVER = 9;
    public static final int PLAY_PAUSE = 10;
    public static final String MOVIE_ITEM_NUM = "movie_item_num";
    public static final String CUR_PLAY_POS = "CUR_PLAY_POS";
    public static final String FAV_CHANGED = "FAV_CHANGED";
    public static final String CID = "CID";
    public static final String DY_HOT_RE_CID = "151";
    public static final String JJ_HOT_RE_CID = "152";
    public static final String ZY_HOT_RE_CID = "153";
    public static final String ZT_HOT_RE_CID = "154";
    public static final String MXZQ_HY_CID = "91";
    public static final String TOPICS_TOP_CID = "155";
    public static final String TOPICS_YY_CID = "156";
    public static final String TOPICS_ZH_CID = "157";
    public static final String TOPICS_YS_CID = "158";
    public static final int PLAY_REQ_CODE = 'P' + 'L' + 'A' + 'Y' + 'R' + 'E' + 'Q' + 'C' + 'O' +
            'D' + 'E';
    public static final int DETAILS_REQ_CODE = 'D' + 'E' + 'T' + 'A' + 'I' + 'L' + 'S' + 'R' + 'E' +
            'Q' + 'C' + 'O' + 'D' + 'E';
    public static final int TOPICS_DETAILS_REQ_CODE = 'T' + 'O' + 'P' + 'I' + 'C' + 'S' + 'D' + 'E'
            + 'T' + 'A' + 'I' + 'L' + 'S' + 'R' + 'E' + 'Q' + 'C' + 'O' + 'D' + 'E';
    public static final String BG_URL = "BG_URL";
    public static final String CKIND_OBJ = "CKIND_OBJ";
    public static final int NEED_YYPY = 1;
    public static final int NEED_BFSJ = 2;
    public static final int NEED_YYPY_BFSJ = 3;
    public static final int NEED_MXZQ = 4;
    public static final int NEED_NONE = -1;
    public static final long TOPICS_TOP_FOCUS_DURATION = 100;
    public static final int PAGE_FAV = 0;
    public static final int PAGE_HISTORY = 1;
    public static final int LIST1_ANIM_DURATION = 300; //300ms
    public static final int SHOW_LIST_TIMEOUT = 10000; //10s
    public static final int PTYPE_FAV_LIVE = -1;
    public static final String VERCODE = "VERCODE";
    public static final int BOTTOM_EPG_TIMEOUT = 5000;
    public static final int SEND_TO_SER_DURATION = 5 * 60 * 1000; //5min
    public static final int APP_TIPS_TYPE = 0;
    public static final int NET_TIPS_TYPE = 1;
    public static final int SHOW_MAINTANCE_TIMEOUT = 15000; // 15s
    public static final long WATCH_TIME = 5 * 60 * 60 * 1000; // 3h
    public static final int RETRY_PLAY_COUNT = 3;
    public static final int RETRY_PLAY_DURATION = 500; // 500 ms
    public static final String AUTO_PLAY = "AUTO_PLAY";
    public static final String TIPS_1 = "TIPS_1";
    public static final String TIPS_2 = "TIPS_2";
    public static final String TIPS_TYPE = "TIPS_TYPE";
    public static final int SURE_KEY_TIMEOUT = 3000; //3s
    public static final int SEND_PLAY_DURATION = 0;
    public static Class[] kindClsArr = new Class[]{
            LiveAct.class,
            PlayBackMainAct.class,
            YYSJAct.class,
            YYSJAct.class,
            SEDMMainAct.class,
            HotReAct.class,
            KindMovieAct.class,
            TopicsMainAct.class,
            SetAct.class,
    };
    public static final String SPEED = "speed";
    public static final String PAGE_TYPE = "page_type";
    public static final String MOVIE_PID = "movie_pid";
}
