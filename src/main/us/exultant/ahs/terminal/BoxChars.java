package us.exultant.ahs.terminal;

/**
 * <p>
 * This interface defines the Unicode box-drawing characters with concise and regularized
 * names.
 * </p>
 * 
 * <h3>Naming Conventions</h3>
 * 
 * <p>
 * All of the box-drawing characters defined herein have been described in three fairly
 * distinct categories: lines, fractional blocks, and quartile blocks.
 * </p>
 * 
 * <p>
 * Lines are those characters typically useful in drawing boundaries. For lines, letters
 * in the name proceed in top-right-bottom-left order, and describe one of four line
 * types:
 * <ul>
 * <li><b>N</b>ull
 * <li><b>S</b>ingle
 * <li><b>H</b>eavy
 * <li><b>D</b>ouble
 * </ul>
 * So, for example, the character "╟" is referred to as "DSDN", because going clockwise
 * from the top it contains a double-stroke, single-stroke, another double-stroke, and
 * then empty space instead of any connective line.
 * </p>
 * 
 * <p>
 * Fractional blocks are characters that most resemble pure rectangular blocks, but have
 * been cut into fractions of a full character space (like these guys: ▁▂▃▄▅▆▇). They're
 * most likely to be useful for building progress bars or something of that ilk. They come
 * in three sub-types:
 * <ul>
 * <li><b>V</b>ertical progression - V1 is a thin line at the bottom of the block, V4
 * takes up the bottom half the block, and V7 is just a hair less than the full block.
 * <li><b>H</b>orizontal progression - H1 is a thin line at the left of the block, H4
 * takes up the left half the block, and H7 is just a hair less than the full block.
 * <li>just solid <b>B</b>locks - B2, B4, B6, and B8, in increasing order of solidness.
 * </ul>
 * (A solid block is the same as {@link #B8}; it could also be seen as both H8 and V8. A
 * space would been the logical progression to stand as V0 or H0.)
 * </p>
 * 
 * <p>
 * Quartile blocks are characters easily seen as the four corners of a full block, in
 * which the corners can be either empty (again represented as a <b>N</b>ull) or solid
 * (<b>B</b>lock). Since the top-right-bottom-left standard doesn't work to describe
 * corners, we take a 1/8th rotation clockwise as our standard &mdash; thus for example
 * "▟" is referred to as "BBBN".
 * </p>
 * 
 * @author hash
 * 
 */
public interface BoxChars {
	// lines
	public static final char	NSNS	= '─';
	public static final char	NHNH	= '━';
	public static final char	SNSN	= '│';
	public static final char	HNHN	= '┃';
	public static final char	NSSN	= '┌';
	public static final char	NHSN	= '┍';
	public static final char	NSHN	= '┎';
	public static final char	NHHN	= '┏';
	public static final char	NNSS	= '┐';
	public static final char	NNSH	= '┑';
	public static final char	NNHS	= '┒';
	public static final char	NNHH	= '┓';
	public static final char	SSNN	= '└';
	public static final char	SHNN	= '┕';
	public static final char	HSNN	= '┖';
	public static final char	HHNN	= '┗';
	public static final char	SNNS	= '┘';
	public static final char	SNNH	= '┙';
	public static final char	HNNS	= '┚';
	public static final char	HNNH	= '┛';
	public static final char	SSSN	= '├';
	public static final char	SHSN	= '┝';
	public static final char	HSSN	= '┞';
	public static final char	SSHN	= '┟';
	public static final char	HSHN	= '┠';
	public static final char	HHSN	= '┡';
	public static final char	SHHN	= '┢';
	public static final char	HHHN	= '┣';
	public static final char	SNSS	= '┤';
	public static final char	SNSH	= '┥';
	public static final char	HNSS	= '┦';
	public static final char	SNHS	= '┧';
	public static final char	HNHS	= '┨';
	public static final char	HNSH	= '┩';
	public static final char	SNHH	= '┪';
	public static final char	HNHH	= '┫';
	public static final char	NSSS	= '┬';
	public static final char	NSSH	= '┭';
	public static final char	NHSS	= '┮';
	public static final char	NHSH	= '┯';
	public static final char	NSHS	= '┰';
	public static final char	NSHH	= '┱';
	public static final char	NHHS	= '┲';
	public static final char	NHHH	= '┳';
	public static final char	SSNS	= '┴';
	public static final char	SSNH	= '┵';
	public static final char	SHNS	= '┶';
	public static final char	SHNH	= '┷';
	public static final char	HSNS	= '┸';
	public static final char	HSNH	= '┹';
	public static final char	HHNS	= '┺';
	public static final char	HHNH	= '┻';
	public static final char	SSSS	= '┼';
	public static final char	SSSH	= '┽';
	public static final char	SHSS	= '┾';
	public static final char	SHSH	= '┿';
	public static final char	HSSS	= '╀';
	public static final char	SSHS	= '╁';
	public static final char	HSHS	= '╂';
	public static final char	HSSH	= '╃';
	public static final char	HHSS	= '╄';
	public static final char	SSHH	= '╅';
	public static final char	SHHS	= '╆';
	public static final char	HHSH	= '╇';
	public static final char	SHHH	= '╈';
	public static final char	HSHH	= '╉';
	public static final char	HHHS	= '╊';
	public static final char	HHHH	= '╋';
	public static final char	NDND	= '═';
	public static final char	DNDN	= '║';
	public static final char	NDSN	= '╒';
	public static final char	NSDN	= '╓';
	public static final char	NDDN	= '╔';
	public static final char	NNSD	= '╕';
	public static final char	NNDS	= '╖';
	public static final char	NNDD	= '╗';
	public static final char	SDNN	= '╘';
	public static final char	DSNN	= '╙';
	public static final char	DDNN	= '╚';
	public static final char	SNND	= '╛';
	public static final char	DNNS	= '╜';
	public static final char	DNND	= '╝';
	public static final char	SDSN	= '╞';
	public static final char	DSDN	= '╟';
	public static final char	DDDN	= '╠';
	public static final char	SNSD	= '╡';
	public static final char	DNDS	= '╢';
	public static final char	DNDD	= '╣';
	public static final char	NDSD	= '╤';
	public static final char	NSDS	= '╥';
	public static final char	NDDD	= '╦';
	public static final char	SDND	= '╧';
	public static final char	DSNS	= '╨';
	public static final char	DDND	= '╩';
	public static final char	SDSD	= '╪';
	public static final char	DSDS	= '╫';
	public static final char	DDDD	= '╬';
	public static final char	NNNS	= '╴';
	public static final char	SNNN	= '╵';
	public static final char	NSNN	= '╶';
	public static final char	NNSN	= '╷';
	public static final char	NNNH	= '╸';
	public static final char	HNNN	= '╹';
	public static final char	NHNN	= '╺';
	public static final char	NNHN	= '╻';
	public static final char	NHNS	= '╼';
	public static final char	SNHN	= '╽';
	public static final char	NSNH	= '╾';
	public static final char	HNSN	= '╿';
	public static final char	NRRN	= '╭';
	public static final char	NNRR	= '╮';
	public static final char	RNNR	= '╯';
	public static final char	RRNN	= '╰';
	
	// fractional blocks
	public static final char	V1	= '▁';
	public static final char	V2	= '▂';
	public static final char	V3	= '▃';
	public static final char	V4	= '▄';
	public static final char	V5	= '▅';
	public static final char	V6	= '▆';
	public static final char	V7	= '▇';
	public static final char	B2	= '░';
	public static final char	B4	= '▒';
	public static final char	B6	= '▓';
	public static final char	B8	= '█';
	public static final char	H7	= '▉';
	public static final char	H6	= '▊';
	public static final char	H5	= '▋';
	public static final char	H4	= '▌';
	public static final char	H3	= '▍';
	public static final char	H2	= '▎';
	public static final char	H1	= '▏';
	
	// quartile blocks
	public static final char	BNNB	= '▀';
	public static final char	BBNN	= '▐';
	public static final char	NBBN	= '▄';	// same as V4
	public static final char	NNBB	= '▌';	// same as H4
	public static final char	NNBN	= '▖';
	public static final char	NBNN	= '▗';
	public static final char	NNNB	= '▘';
	public static final char	NBBB	= '▙';
	public static final char	NBNB	= '▚';
	public static final char	BNBB	= '▛';
	public static final char	BBNB	= '▜';
	public static final char	BNNN	= '▝';
	public static final char	BNBN	= '▞';
	public static final char	BBBN	= '▟';
	
	// i haven't yet incorporated any of the following:
	//	
	//	╳
	//	╲
	//	▕
	//	╱
	//	▔
	//	
	//	┄
	//	┅
	//	┆
	//	┇
	//	┈
	//	┉
	//	┊
	//	┋
	//	╌
	//	╍
	//	╎
	//	╏
}
