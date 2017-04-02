/**
 * File: TemplateVisitor.java
 * @author Edward (osan) Zhou
 * @author Alex Rinker
 * @author Vivek Sah
 * Class: CS461
 * Project: 4A
 * Date: March 30 2017
 */

package bantam.codegenmips;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SemanticTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


/**
 * This class generates all of the templates for the
 * MIPS file based on user and built-in classes
 */
public class TemplateGenerator extends bantam.visitor.Visitor{

    /**
     * This field stores the fields of the current class being
     * iterated through
     */
    private ArrayList<String> currentFields;

    /**
     * This field stores the fields of the parent of the class being
     * iterated through
     */
    private ArrayList<String> parentFields;

    /**
     * this field keeps track of what label is associated with what class
     */
    private Map<String,String> classLabels;

    /**
     * this is the MipsSupport tool used to facilitate generation of templates
     */
    private MipsSupport assemblySupport;

    /**
     * Generates all of the class templates based on the
     * program associated with the given root node.
     * @param root
     * @param assemblySupport the support tool to help template generation
     */
    public void generateClassTemplates(ClassTreeNode root,
                                       MipsSupport assemblySupport,
                                       Map<String,String> classLabels) {
        this.currentFields = new ArrayList<>();
        this.parentFields = new ArrayList<>();
        this.classLabels = classLabels;
        this.assemblySupport = assemblySupport;
        generateTemplates(root);
        return;
    }

    /**
     * Accepts the visitor for the parent node and then
     * recurses through each of the parent's children
     * @param parent
     */
    private void generateTemplates(ClassTreeNode parent) {
        parent.getASTNode().accept(this);
        parentFields.addAll(this.currentFields);

        //Keep track of the fields we added, to remove them when we recurse back
        ArrayList<String> fieldsToRemove = new ArrayList<>(this.currentFields);
        this.currentFields.clear();

        parent.getChildrenList().forEachRemaining( child -> {
            generateTemplates(child);
        });
        this.parentFields.removeAll(fieldsToRemove);
        this.currentFields.clear();
    }


    /**
     * Visits the class node and generates the object template
     * based on it's fields
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        super.visit(node);

        //generate the text based on the fields, name, and size
        this.assemblySupport.genLabel(node.getName() + "_template");
        this.assemblySupport.genWord(this.classLabels.get(node.getName()));

        //Determine the byte size of the object template
        int size = 12 + 4*(parentFields.size() + currentFields.size());
        this.assemblySupport.genWord(String.valueOf(size));

        this.assemblySupport.genWord(node.getName() + "_dispatch_table");

        //Add all Parent fields
        for(String field : this.parentFields) {
            this.assemblySupport.genWord(field);
        }

        //Add all of this class' fields
        for(String field : this.currentFields) {
            this.assemblySupport.genWord(field);
        }

        return null;
    }

    /**
     * updates the "field" fields based on the current class' fields
     * @param node the member node
     * @return
     */
    @Override
    public Object visit(Field node) {
        this.currentFields.add("0");
        return null;
    }

    //Stop if you get to a Member
    @Override
    public Object visit(Member node) {return null;}

}
