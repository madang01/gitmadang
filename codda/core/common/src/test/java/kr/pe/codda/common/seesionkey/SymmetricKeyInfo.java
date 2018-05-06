package kr.pe.codda.common.seesionkey;

public class SymmetricKeyInfo {
	private String symmetricKeyAlgorithm;
	private int symmetricKeySize;
	private int ivSize;
	
	public SymmetricKeyInfo(String symmetricKeyAlgorithm, int symmetricKeySize, int ivSize) {
		super();
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeySize = symmetricKeySize;
		this.ivSize = ivSize;
	}
	public String getSymmetricKeyAlgorithm() {
		return symmetricKeyAlgorithm;
	}
	public int getSymmetricKeySize() {
		return symmetricKeySize;
	}
	public int getIvSize() {
		return ivSize;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SymmetricKeyInfo [symmetricKeyAlgorithm=");
		builder.append(symmetricKeyAlgorithm);
		builder.append(", symmetricKeySize=");
		builder.append(symmetricKeySize);
		builder.append(", ivSize=");
		builder.append(ivSize);
		builder.append("]");
		return builder.toString();
	}
}
