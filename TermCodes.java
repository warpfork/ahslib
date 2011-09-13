package us.exultant.grid.terminal;

public class TermCodes {
	public static final String	CSI			= "\033[";
	
	public static final String	COLOR_FG_BLACK		= CSI+"30m";
	public static final String	COLOR_FG_RED		= CSI+"31m";
	public static final String	COLOR_FG_GREEN		= CSI+"32m";
	public static final String	COLOR_FG_YELLOW		= CSI+"33m";
	public static final String	COLOR_FG_BLUE		= CSI+"34m";
	public static final String	COLOR_FG_MAGENTA	= CSI+"35m";
	public static final String	COLOR_FG_CYAN		= CSI+"36m";
	public static final String	COLOR_FG_WHITE		= CSI+"37m";
	public static final String	COLOR_FG_BRIGHTBLACK	= CSI+"1;30m";
	public static final String	COLOR_FG_BRIGHTRED	= CSI+"1;31m";
	public static final String	COLOR_FG_BRIGHTGREEN	= CSI+"1;32m";
	public static final String	COLOR_FG_BRIGHTYELLOW	= CSI+"1;33m";
	public static final String	COLOR_FG_BRIGHTBLUE	= CSI+"1;34m";
	public static final String	COLOR_FG_BRIGHTMAGENTA	= CSI+"1;35m";
	public static final String	COLOR_FG_BRIGHTCYAN	= CSI+"1;36m";
	public static final String	COLOR_FG_BRIGHTWHITE	= CSI+"1;37m";
	public static final String	COLOR_FG_DEFAULT	= CSI+"39m";
	
	
	public static final String	COLOR_BG_BLACK		= CSI+"40m";
	public static final String	COLOR_BG_RED		= CSI+"41m";
	public static final String	COLOR_BG_GREEN		= CSI+"42m";
	public static final String	COLOR_BG_YELLOW		= CSI+"43m";
	public static final String	COLOR_BG_BLUE		= CSI+"44m";
	public static final String	COLOR_BG_MAGENTA	= CSI+"45m";
	public static final String	COLOR_BG_CYAN		= CSI+"46m";
	public static final String	COLOR_BG_WHITE		= CSI+"47m";
	public static final String	COLOR_BG_DEFAULT	= CSI+"49m";
	
	public static final String	WHAT_WHAT		= CSI+"";
}
