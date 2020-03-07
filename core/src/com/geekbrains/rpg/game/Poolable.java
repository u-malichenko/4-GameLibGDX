package com.geekbrains.rpg.game;

/**
 * говорит о том что тот класс который им помечен может быть засунут в пулл объектов
 */
public interface Poolable {
    boolean isActive();//проверка активности, для реализации освобождения объекта в пул иннактиве
}