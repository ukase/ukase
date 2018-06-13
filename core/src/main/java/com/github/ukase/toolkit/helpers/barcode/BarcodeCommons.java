/*
 * Copyright (c) 2018 Pavel Uvarov <pauknone@yahoo.com>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.toolkit.helpers.barcode;

import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Base64;

@Slf4j
class BarcodeCommons {
    private static final String FILE_TYPE = "png";
    private static final String IMAGE_SRC_PREFIX = "data:image/png;base64,";

    static String bitMatrixToImage(BitMatrix matrix) throws IOException {
        BufferedImage image = renderImage(matrix, matrix.getWidth(), matrix.getHeight());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(image, FILE_TYPE, buffer);

        return IMAGE_SRC_PREFIX + Base64.getEncoder().encodeToString(buffer.toByteArray());
    }

    static String convertData(String qrString, String characterEncoding) {
        Charset charset = Charset.forName(characterEncoding);
        CharsetEncoder encoder = charset.newEncoder();
        if (!characterEncoding.equalsIgnoreCase("UTF-8")) {
            try {
                ByteBuffer buf = encoder.encode(CharBuffer.wrap(qrString));
                byte[] b = buf.array();
                return new String(b, characterEncoding);
            } catch (CharacterCodingException e) {
                log.error("Can't convert to specified character encoding!", e);
            } catch (UnsupportedEncodingException e) {
                log.error("Unsupported encoding!", e);
            }
        }
        return qrString;
    }

    private static BufferedImage renderImage(BitMatrix matrix, int barcodeWidth, int barcodeHeight) {
        BufferedImage image = new BufferedImage(barcodeWidth, barcodeHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, barcodeWidth, barcodeHeight);

        graphics.setColor(Color.BLACK);
        for (int x = 0; x < barcodeWidth; x++) {
            for (int y = 0; y < barcodeHeight; y++) {
                if (matrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        return image;
    }
}
