package us.exultant.ahs.terminal.geom;

public class Margin {
	public Margin(int $margin) {
		this($margin, $margin, $margin, $margin);
	}
	
	public Margin(int $vertical, int $horizontal) {
		this($vertical, $horizontal, $vertical, $horizontal);
	}
	
	public Margin(int $top, int $right, int $bottom, int $left) {
		this.$top = $top;
		this.$right = $right;
		this.$bottom = $bottom;
		this.$left = $left;
	}
	
	int	$top;
	int	$right;
	int	$bottom;
	int	$left;
	
	public int getTop() {
		return this.$top;
	}
	
	public int getRight() {
		return this.$right;
	}
	
	public int getBottom() {
		return this.$bottom;
	}
	
	public int getLeft() {
		return this.$left;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.$bottom;
		result = prime * result + this.$left;
		result = prime * result + this.$right;
		result = prime * result + this.$top;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Margin other = (Margin) obj;
		if (this.$bottom != other.$bottom) return false;
		if (this.$left != other.$left) return false;
		if (this.$right != other.$right) return false;
		if (this.$top != other.$top) return false;
		return true;
	}
}
