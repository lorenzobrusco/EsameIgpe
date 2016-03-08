package editor;

import java.io.File;
import java.io.IOException;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;

public class SaveTerrain {

	private Node modelToSave;

	public SaveTerrain(Node modelToSave) {
		this.modelToSave = modelToSave;
	}

	public void saveModel(String name) {

		String nameTmp = name;
		File file = new File("assets" + File.separator + "Scenes" + File.separator + nameTmp + ".j3o");
		if (file.exists()) {
			file.delete();
		}
		file = new File("assets" + File.separator + "Scenes" + File.separator + nameTmp + ".j3o");
		BinaryExporter exporter = new BinaryExporter();
		try {
			exporter.save(modelToSave, file);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
