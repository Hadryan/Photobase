package utils;

import java.util.ArrayList;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import entities.Images;
import utils.MultipartFile.CategoryInput;
import utils.MultipartFile.ResolutionInput;

public class MultipartApplicationFile {

	private ArrayList<byte[]> data;
	
	public MultipartApplicationFile() {
		data = new ArrayList<>();
	}
	
	@FormParam("data1")
	@PartType("application/octet-stream")
	public void setData1(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data2")
	@PartType("application/octet-stream")
	public void setData2(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data3")
	@PartType("application/octet-stream")
	public void setData3(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data4")
	@PartType("application/octet-stream")
	public void setData4(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data5")
	@PartType("application/octet-stream")
	public void setData5(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data6")
	@PartType("application/octet-stream")
	public void setData6(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data7")
	@PartType("application/octet-stream")
	public void setData7(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data8")
	@PartType("application/octet-stream")
	public void setData8(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data9")
	@PartType("application/octet-stream")
	public void setData9(byte[] data) {
		this.data.add(data);
	}
	
	@FormParam("data10")
	@PartType("application/octet-stream")
	public void setData10(byte[] data) {
		this.data.add(data);
	}
	
	public ArrayList<byte[]> getData() {
		return data;
	}
	
}
