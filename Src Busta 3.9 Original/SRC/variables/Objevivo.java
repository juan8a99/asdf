
package variables;

public class Objevivo {
	private int id;
	private int comidaAño;
	private int comidaFecha;
	private int comidaHora;
	private int humor;
	private int mascara;
	private int tipo;
	private int itemId;
	private long xp;
	private int interAño;
	private int interFecha;
	private int interHora;
	private int adqAño;
	private int adqFecha;
	private int adqHora;
	private int asociado;// asociado algun item 0 = no, 1 = si
	private int realModeloDB;// item original (objevivo 9233 capa, 9234 sombrero, 9255 amuleto, 9256 anillo
	private int itemObjevivo; // item del objevivo original
	private String stats;
	
	public Objevivo(int ID, int comidaAno, int comidaFecha, int comidaHora, int humor, int mascara, int tipo, int itemID,
			long XP, int interAno, int interFecha, int interHoras, int adqAno, int adqFecha, int adqHora, int asociado,
			int realModeloDB, int objevivo, String stats) {
		this.id = ID;
		this.comidaAño = comidaAno;
		this.comidaFecha = comidaFecha;
		this.comidaHora = comidaHora;
		this.humor = humor;
		this.mascara = mascara;
		this.tipo = tipo;
		this.itemId = itemID;
		this.xp = XP;
		this.interAño = interAno;
		this.interFecha = interFecha;
		this.interHora = interHoras;
		this.adqAño = adqAno;
		this.adqFecha = adqFecha;
		this.adqHora = adqHora;
		this.asociado = asociado;
		this.realModeloDB = realModeloDB;
		this.itemObjevivo = objevivo;
		this.stats = stats;
	}
	
	public int getID() {
		return id;
	}
	
	public int getFeedYears() {
		return comidaAño;
	}
	
	public void setFeedYears(int feedyears) {
		this.comidaAño = feedyears;
	}
	
	public int getFeedDate() {
		return comidaFecha;
	}
	
	public void setFeedDate(int feeddate) {
		this.comidaFecha = feeddate;
	}
	
	public int getFeedHours() {
		return comidaHora;
	}
	
	public void setFeedHours(int feedhours) {
		this.comidaHora = feedhours;
	}
	
	public int getHumeur() {
		return humor;
	}
	
	public void setHumeur(int humeurId) {
		this.humor = humeurId;
	}
	
	public int getMascara() {
		return mascara;
	}
	
	public void setSkin(int skinId) {
		this.mascara = skinId;
	}
	
	public int getType() {
		return tipo;
	}
	
	public void setType(int typeId) {
		this.tipo = typeId;
	}
	
	public int getItemID() {
		return itemId;
	}
	
	public void setItemId(int itemid) {
		this.itemId = itemid;
	}
	
	public long getXp() {
		return xp;
	}
	
	public void setXp(long XP) {
		this.xp = XP;
	}
	
	public int getToYears() {
		return interAño;
	}
	
	public void setToYears(int toyears) {
		this.interAño = toyears;
	}
	
	public int getToDate() {
		return interFecha;
	}
	
	public void setToDate(int todate) {
		this.interFecha = todate;
	}
	
	public int getToHours() {
		return interHora;
	}
	
	public void setToHours(int tohours) {
		this.interHora = tohours;
	}
	
	public int getHasYears() {
		return adqAño;
	}
	
	public void setHasYears(int hasyears) {
		this.adqAño = hasyears;
	}
	
	public int getHasDate() {
		return adqFecha;
	}
	
	public void setHasDate(int hasdate) {
		this.adqFecha = hasdate;
	}
	
	public int getHasHours() {
		return adqHora;
	}
	
	public void setHasHours(int hashours) {
		this.adqHora = hashours;
	}
	
	public int getAsociado() {
		return asociado;
	}
	
	public void setAsociado(int asociado) {
		this.asociado = asociado;
	}
	
	public int getrealtemplate() {
		return realModeloDB;
	}
	
	public void setRealModeloDB(int modelo) {
		this.realModeloDB = modelo;
	}
	
	public int getItemObjevivo() {
		return itemObjevivo;
	}
	
	public void setItemObjevivo(int itemObjevivo) {
		this.itemObjevivo = itemObjevivo;
	}
	
	public String getStat() {
		return stats;
	}
	
	public void setStat(String stat) {
		this.stats = stat;
	}
	
	public String convertirAString() {
		String toReturn = "328#" + Integer.toHexString(this.comidaAño) + "#" + Integer.toHexString(this.comidaFecha) + "#"
				+ Integer.toHexString(this.comidaHora) + "," + "3cb#0#0#" + Integer.toBinaryString(this.humor) + "," + "3cc#0#0#"
				+ Integer.toHexString(this.mascara) + "," + "3cd#0#0#" + Integer.toHexString(this.tipo) + "," + "3ca#0#0#"
				+ Integer.toHexString(this.realModeloDB) + "," + "3ce#0#0#" + Long.toHexString(this.xp) + "," + "3d7#"
				+ Integer.toHexString(this.interAño) + "#" + Integer.toHexString(this.interFecha) + "#"
				+ Integer.toHexString(this.interHora) + "," + "325#" + Integer.toHexString(this.adqAño) + "#"
				+ Integer.toHexString(this.adqFecha) + "#" + Integer.toHexString(this.adqHora);
		return toReturn;
	}
}
