package view;

import java.util.List;

import core.Navigation;
import model.common.Listable;
import service.exception.ServiceException;
import service.exception.ValidationException;
import utils.ConsoleUtils;
import utils.MenuRenderer;

public abstract class BaseView {

    public int renderBanner(String... options) {
        ConsoleUtils.clearScreen();
        MenuRenderer.renderBanner(Navigation.getPath());
        MenuRenderer.renderOptions(options);
        return ConsoleUtils.readInteger("Escolha: ");
    }

    public <T extends Listable> void renderListEntity(List<T> entities) {
        for (T e : entities) {
            MenuRenderer.renderMessageLine(e.getId() + " - " + e.getName());
        }
    }

    public void renderMessage(String msg) {
        MenuRenderer.renderMessage(msg);
    }

    public void renderMessageLine(String msg) {
        MenuRenderer.renderMessageLine(msg);
    }

    public void renderError(String msg) {
        MenuRenderer.renderError(msg);
    }

    public void renderValidationException(ValidationException e) {
        MenuRenderer.renderValidationException(e);
    }

    public void renderServiceException(ServiceException e) {
        MenuRenderer.renderServiceException(e);
    }

    public void renderException(Exception e) {
        MenuRenderer.renderException(e);
    }

    public <T extends Listable> void displayEntityList(List<T> entityList, String entityName) {
        System.out.println("\n[ " + entityName.toUpperCase() + " DISPONÍVEIS ]");

        if (entityList == null || entityList.isEmpty()) {
            System.out.println("Nenhum item cadastrado.");
            return;
        }

        for (T entity : entityList) {
            System.out.println("ID: " + entity.getId() + " - " + entity.getName());
        }
    }

}
