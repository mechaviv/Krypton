/*
 *     This file is part of Development, a MapleStory Emulator Project.
 *     Copyright (C) 2015 Eric Smith <muffinman75013@yahoo.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 */

package game.user.stat;

import network.packet.InPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * CFlag
 *
 * @author Eric
 */
public final class Flag {
    public static final int
            INT_32 = 32,
            INT_64 = 64,
            INT_96 = 96,
            INT_128 = 128,
            INT_160 = 160,
            INT_192 = 192,
            INT_224 = 224,
            INT_256 = 256,
            INT_288 = 288,
            INT_320 = 320,
            INT_352 = 352,
            INT_384 = 384,
            INT_416 = 416,
            INT_448 = 448,
            INT_480 = 480,
            INT_512 = 512,
            INT_544 = 544,
            INT_576 = 576,
            INT_608 = 608,
            INT_640 = 640,
            DEFAULT = INT_128 // Most common versions used are UINT128.
                    ;
    private final int[] data;

    /**
     * Construct a by-default Flag base (128-bit)
     */
    public Flag() {
        this(DEFAULT);
    }

    /**
     * Construct a specific Flag of bits.
     * Please refer to the common bits used for versions/collections.
     *
     * [MENTION=2000183830]param[/MENTION] bits The number of bits this flag will contain
     */
    public Flag(int bits) {
        this.data = new int[bits >> 5];
        this.setValue(0);
    }

    /**
     * Your standard copy-constructor.
     *
     * [MENTION=2000183830]param[/MENTION] value The flag to replicate/copy.
     * [MENTION=2000183830]param[/MENTION] numBits
     */
    public Flag(Flag value, int numBits) {
        this(32 * value.data.length);
        // Copy the 32bit chunk
        for (int i = (numBits >> 5); i > 0; i--) {
            this.data[i - 1] = value.data[i - 1];
        }
        // Copy the remaining bits
        for (int i = 32 * (numBits >> 5); i < numBits; i++) {
            this.setBitNumber(i, value.getBitNumber(i));
        }
        // Pad the remaining bits of the 32bit chunk with randoms
        for (int i = numBits; i < (32 * this.data.length); i++) {
            int rand = ((214013 * ThreadLocalRandom.current().nextInt(32767) + 2531011) >> 16) & 0x7FFF;
            this.setBitNumber(i, rand % 2);
        }
    }

    /**
     * Compares the flag against an incoming value. If the flag
     * is less than the value, it returns -1. If the flag is
     * greater than the value, it returns 1. It will otherwise
     * return 0 marking the flag as equal to the value.
     *
     * [MENTION=2000183830]para[/MENTION]m other The value to compare the flag against
     * [MENTION=850422]return[/MENTION] If the flag is less than, greater than, or equal to
     */
    public int compareTo(Flag other) {
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] < other.data[i])
                return -1;
            if (this.data[i] > other.data[i])
                return 1;
        }
        return 0;
    }

    /**
     * Compares the flag against an incoming value. If the flag
     * is less than the value, it returns -1. If the flag is
     * greater than the value, it returns 1. It will otherwise
     * return 0 marking the flag as equal to the value.
     *
     * [MENTION=2000183830]para[/MENTION]m value The value to compare the flag against
     * [MENTION=850422]return[/MENTION] If the flag is less than, greater than, or equal to
     */
    public int compareTo(int value) {
        int len = this.data.length - 1;
        if (this.data[len] > value)
            return 1;
        for (int i = 0; i < len; i++) {
            if (this.data[i] != 0)
                return 1;
        }
        return -((this.data[len] < value) ? 1 : 0);
    }

    /**
     * Decodes a Flag from an InPacket stream, only after knowing
     * the amount of bits the Flag contains.
     *
     * [MENTION=2000183830]para[/MENTION]m packet The InPacket buffer
     */
    public void decodeBuffer(InPacket packet) {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = packet.decodeInt();
        }
    }

    /**
     * Determines if the flag has been set for the specific bit.
     *
     * [MENTION=2000183830]para[/MENTION]m bit The bit number to validate
     * [MENTION=850422]return[/MENTION] If the flag for the specific bit has been set
     */
    public int getBitNumber(int bit) {
        if (bit < (32 * this.data.length)) {
            return (this.data[bit >> 5] >> (31 - (bit & 0x1F))) & 1;
        }
        return 0;
    }

    /**
     * Returns the flag's {m_uData} array containing all
     * currently present bits. The data is not to be modified.
     *
     * [MENTION=850422]return[/MENTION] The flag's {m_uData}
     */
    public final int[] getData() {
        return this.data;
    }

    /**
     * If any of the bits within the flag is NOT zero,
     * then the flag is considered set and not empty.
     *
     * [MENTION=850422]return[/MENTION] If the flag is not zero
     */
    public boolean isSet() {//operator_bool
        int i = 0;
        int len = this.data.length - 1;
        while (this.data[i] == 0) {
            if (++i >= len) {
                return this.data[len] != 0;
            }
        }
        return true;
    }

    /**
     * If all of the data contained within this flag is
     * equal to zero, then the flag is zero.
     *
     * [MENTION=850422]return[/MENTION] If the Flag is zero
     */
    public boolean isZero() {//operator!
        int i = this.data.length - 1;
        while (this.data[i] == 0) {
            if (--i < 0)
                return true;
        }
        return false;
    }

    /**
     * Performs an AND (&=) operation on every bit
     * between the two flags and returns the result
     * of the bitwise AND operation.
     *
     * [MENTION=2000183830]para[/MENTION]m value The flag to & the bits of
     * [MENTION=850422]return[/MENTION] The result of the AND operation
     */
    public Flag operatorAND(Flag value) {//operator&
        if (this.data.length != value.data.length) {
            return null;
        }
        int len = this.data.length;
        Flag temp = new Flag(32 * len);
        for (int i = len; i >= 1; i--)
            temp.data[i - 1] = value.data[i - 1] & this.data[i - 1];
        return temp;
    }

    /**
     * Performs the "equals" operation on every bit
     * where as such it will return true if:
     *      uData <= value AND uData >= value
     *
     * [MENTION=2000183830]para[/MENTION]m value The value to compare against the current bits
     * [MENTION=850422]return[/MENTION] If the value is equal
     */
    public boolean isEqual(int value) {//operator==
        int i = 0;
        int len = this.data.length - 1;
        while (this.data[i] == 0) {
            if (++i >= len) {
                int data = this.data[len];
                if (data <= value)
                    return data >= value;
                return false;
            }
        }
        return false;
    }

    /**
     * Performs a bitwise OR directly on this instance.
     * For a returned OR temp Flag result, refer to {OperatorOR}.
     *
     * [MENTION=2000183830]para[/MENTION]m value The flag to |= the bits of
     */
    public void performOR(Flag value) {//operator=|
        if (this.data.length != value.data.length) {
            return;
        }
        int i = 0;
        int len = value.data.length - 1;
        while (value.data[i] == 0) {
            if (++i >= len) {
                if (value.data[len] == 0)
                    return;
                break;
            }
        }
        for (int j = len; j >= 0; j--) {
            this.data[j] |= value.data[j];
        }
    }

    /**
     * Performs an OR (|=) operation on every bit
     * between the two flags and returns the result
     * of the bitwise OR operation.
     *
     * [MENTION=2000183830]para[/MENTION]m value The flag to | the bits of
     * [MENTION=850422]return[/MENTION] The result of the OR operation
     */
    public Flag operatorOR(Flag value) {
        if (this.data.length != value.data.length) {
            return null;
        }
        int len = this.data.length;
        Flag temp = new Flag(32 * len);
        for (int i = len; i >= 0; i--)
            temp.data[i - 1] = value.data[i - 1] | this.data[i - 1];
        return temp;
    }

    /**
     * Shifts the flag left by {bits} and readjusts the {m_uData} array.
     *
     * [MENTION=2000183830]para[/MENTION]m bits The amount of bits to shift left
     */
    public void shiftLeft(int bits) {
        //System.out.println("Length = " + data.length);
        if (bits == 0 || isZero())
            return;
        int len = this.data.length;
        if (bits >= (32 * len)) {
            setValue(0);
            return;
        }
        int[] newBits = new int[len];
        for (int i = 0; i < newBits.length; i++)
            newBits[i] = 0;
        int iShift = bits >> 5;
        if (iShift < len) {
            long uShift = 0;
            for (int i = (len - 1); i >= iShift; i--) {
                uShift += ((long)this.data[i] << (bits & 0x1F)) + (uShift >> 32);
                //System.out.println("New Bit Pos = " + (i - iShift));
                newBits[i - iShift] = (int) uShift;
                uShift >>= 32;
            }
        }
        System.arraycopy(newBits, 0, this.data, 0, len);
    }

    /**
     * Updates or sets the new value/flag of a specific bit.
     *
     * [MENTION=2000183830]para[/MENTION]m bit The selected bit to update
     * [MENTION=2000183830]para[/MENTION]m value The new value to be set
     */
    public void setBitNumber(int bit, int value) {
        int mask = 1 << (31 - (bit & 0x1F));
        int longNum = bit >> 5;
        this.data[longNum] |= mask;
        if (value == 0)
            this.data[longNum] = mask ^ this.data[longNum];
    }

    /**
     * Single-handled ShiftLeft operation used on
     * higher versions due to the lack of support
     * with UINT128::shiftLeft.
     *
     * Old:
     * SetValue(1)
     * ShiftLeft(bits)
     *
     * New:
     * SetData(bits)
     *
     * [MENTION=2000183830]para[/MENTION]m bits The bit value to OR
     */
    public void setData(int bits) {
        int len = this.data.length - 1;
        int index = len - (bits >> 5);
        int value = 1 << (0x1F - (bits & 0x1F));
        this.data[index] |= value;
    }

    /**
     * Assigns a new value to this flag.
     *
     * [MENTION=2000183830]para[/MENTION]m value The value to set the flag to
     */
    public void setValue(int value) {
        int len = this.data.length - 1;
        for (int i = 0; i < len; i++)
            this.data[i] = 0;
        this.data[len] = value;
    }

    /**
     * Returns a byte array representation of the
     * current {m_uData} integer array.
     *
     * [MENTION=850422]return[/MENTION] {data} as byte[]
     */
    public byte[] toByteArray() {
        return toByteArray(false);
    }

    public void setEncodeAll() {
        for (int i = 0; i < data.length; i++) {
            data[i] = -1;
        }
    }
    /**
     * Returns a byte array representation of the current
     * {uData} integer appropriate for the specified flag
     * as an array.
     *
     * [MENTION=2000183830]para[/MENTION]m newVer If you no longer utilize UINT and require backwards destination
     * [MENTION=850422]return[/MENTION] {aData} as a byte[]
     */
    public byte[] toByteArray(boolean newVer) {
        if (newVer)
            return toByteArrayEx();
        int len = this.data.length * 4;
        byte[] dest = new byte[len];

        for (int i = this.data.length; i >= 1; i--) {
            int data = this.data[i - 1];

            dest[--len] = (byte) ((data >>> 24) & 0xFF);
            dest[--len] = (byte) ((data >>> 16) & 0xFF);
            dest[--len] = (byte) ((data >>> 8) & 0xFF);
            dest[--len] = (byte) (data & 0xFF);
        }
        return dest;
    }

    /**
     * Simply put: The reverse of ToByteArray
     *
     * [MENTION=850422]return[/MENTION] {aData} as a byte[]
     */
    public byte[] toByteArrayEx() {
        int len = 0;
        byte[] dest = new byte[this.data.length * 4];

        for (int i = this.data.length; i >= 1; i--) {
            int data = this.data[i - 1];

            dest[len++] = (byte)(data & 0xFF);
            dest[len++] = (byte)((data >>> 8) & 0xFF);
            dest[len++] = (byte)((data >>> 16) & 0xFF);
            dest[len++] = (byte)((data >>> 24) & 0xFF);
        }
        return dest;
    }

    /**
     * Represents this flag's current {m_uData} value as a
     * readable hexadecimal string.
     *
     * [MENTION=850422]return[/MENTION] The flag's value in a base-16 string
     */
    public String toHexString() {
        String sData = "0x";
        for (int i = 0; i < this.data.length; i++) {
            sData += String.format("%08X", this.data[i]);
        }
        return sData;
    }
}