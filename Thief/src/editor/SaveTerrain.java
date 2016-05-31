package editor;

import java.io.File;
import java.io.IOException;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;

/**
 * 
 * This class save landscape in a file .j3o
 *
 */

public class SaveTerrain {

    /** model to save */
    private Node modelToSave;

    public SaveTerrain(Node modelToSave) {
	this.modelToSave = modelToSave;
    }

    /** this method save file */
    public void saveModel(String name) throws IOException {
	File file = new File("assets" + File.separator + "Scenes" + File.separator + name + ".j3o");
	BinaryExporter exporter = new BinaryExporter();
	exporter.save(modelToSave, file);
    }

}
