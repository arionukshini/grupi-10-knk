package com.company.system.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardNavigation {

    private static final String INSTALLED_KEY = "keyboardNavigationInstalled";

    private KeyboardNavigation() {}

    public static void install(Scene scene) {
        if (scene == null || Boolean.TRUE.equals(scene.getProperties().get(INSTALLED_KEY))) {
            return;
        }

        scene.getProperties().put(INSTALLED_KEY, true);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB && !event.isAltDown() && !event.isControlDown() && !event.isMetaDown()) {
                moveFocus(scene.getRoot(), event.isShiftDown());
                event.consume();
            }
        });
    }

    public static void install(Node node) {
        if (node == null) {
            return;
        }

        node.sceneProperty().addListener((obs, oldScene, newScene) -> install(newScene));
        install(node.getScene());
    }

    public static void focusFirst(Node node) {
        Platform.runLater(() -> {
            List<Node> focusableNodes = focusableNodes(node);
            if (!focusableNodes.isEmpty()) {
                focusableNodes.get(0).requestFocus();
            }
        });
    }

    private static void moveFocus(Parent root, boolean reverse) {
        if (root == null) {
            return;
        }

        Node focused = root.getScene() == null ? null : root.getScene().getFocusOwner();
        Parent traversalRoot = traversalRoot(root, focused);
        List<Node> focusableNodes = focusableNodes(traversalRoot);
        if (focusableNodes.isEmpty()) {
            return;
        }

        int currentIndex = focusedIndex(focusableNodes, focused);
        int nextIndex;

        if (currentIndex < 0) {
            nextIndex = nearestFocusableIndex(traversalRoot, focusableNodes, focused, reverse);
        } else if (reverse) {
            nextIndex = (currentIndex - 1 + focusableNodes.size()) % focusableNodes.size();
        } else {
            nextIndex = (currentIndex + 1) % focusableNodes.size();
        }

        focusableNodes.get(nextIndex).requestFocus();
    }

    private static Parent traversalRoot(Parent sceneRoot, Node focused) {
        Node cursor = focused;
        while (cursor != null) {
            if (cursor instanceof Parent parent && isPageRoot(parent)) {
                return parent;
            }
            cursor = cursor.getParent();
        }
        return sceneRoot;
    }

    private static boolean isPageRoot(Parent parent) {
        return parent.getStyleClass().contains("module-page")
                || parent.getStyleClass().contains("profile-page")
                || parent.getStyleClass().contains("departments-page")
                || parent.getStyleClass().contains("wizard-root");
    }

    private static int nearestFocusableIndex(Parent traversalRoot, List<Node> focusableNodes, Node focused, boolean reverse) {
        List<Node> orderedNodes = visibleNodes(traversalRoot);
        int focusedOrder = orderedIndex(orderedNodes, focused);

        if (focusedOrder < 0) {
            return reverse ? focusableNodes.size() - 1 : 0;
        }

        if (reverse) {
            for (int i = focusableNodes.size() - 1; i >= 0; i--) {
                if (orderedIndex(orderedNodes, focusableNodes.get(i)) < focusedOrder) {
                    return i;
                }
            }
            return focusableNodes.size() - 1;
        }

        for (int i = 0; i < focusableNodes.size(); i++) {
            if (orderedIndex(orderedNodes, focusableNodes.get(i)) > focusedOrder) {
                return i;
            }
        }
        return 0;
    }

    private static int focusedIndex(List<Node> focusableNodes, Node focused) {
        Node cursor = focused;
        while (cursor != null) {
            int index = focusableNodes.indexOf(cursor);
            if (index >= 0) {
                return index;
            }
            cursor = cursor.getParent();
        }
        return -1;
    }

    private static List<Node> focusableNodes(Node root) {
        List<Node> result = new ArrayList<>();
        collectFocusableNodes(root, result);
        return result;
    }

    private static List<Node> visibleNodes(Node root) {
        List<Node> result = new ArrayList<>();
        collectVisibleNodes(root, result);
        return result;
    }

    private static int orderedIndex(List<Node> orderedNodes, Node node) {
        Node cursor = node;
        while (cursor != null) {
            int index = orderedNodes.indexOf(cursor);
            if (index >= 0) {
                return index;
            }
            cursor = cursor.getParent();
        }
        return -1;
    }

    private static void collectFocusableNodes(Node node, List<Node> result) {
        if (node == null || !isVisibleAndEnabled(node)) {
            return;
        }

        if (isFocusableControl(node)) {
            result.add(node);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectFocusableNodes(child, result);
            }
        }
    }

    private static void collectVisibleNodes(Node node, List<Node> result) {
        if (node == null || !isVisibleAndEnabled(node)) {
            return;
        }

        result.add(node);

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectVisibleNodes(child, result);
            }
        }
    }

    private static boolean isVisibleAndEnabled(Node node) {
        return node.isVisible()
                && !node.isDisabled()
                && node.isManaged()
                && node.getOpacity() > 0;
    }

    private static boolean isFocusableControl(Node node) {
        if (!(node instanceof Control control) || !control.isFocusTraversable()) {
            return false;
        }

        return node instanceof ButtonBase
                || node instanceof ComboBoxBase<?>
                || node instanceof TextInputControl
                || control.getClass().getName().endsWith("Hyperlink");
    }
}
