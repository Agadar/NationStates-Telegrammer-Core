package com.github.agadar.telegrammer.core.misc;

/**
 * Enumerator for the state of the telegrammer core library.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public enum TelegrammerState {
    IDLE,
    COMPILING_RECIPIENTS,
    QUEUING_TELEGRAMS;
}
