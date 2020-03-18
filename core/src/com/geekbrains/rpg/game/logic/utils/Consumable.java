package com.geekbrains.rpg.game.logic.utils;

import com.geekbrains.rpg.game.logic.GameCharacter;

/**
 * для высыпания оружия или монеток
 */
public interface Consumable {
    void consume(GameCharacter gameCharacter);
}
