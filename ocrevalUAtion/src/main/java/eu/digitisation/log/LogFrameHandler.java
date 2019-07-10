/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author R.C.C.
 */
public class LogFrameHandler extends Handler {

    private LogFrame frame = new LogFrame();

    @Override
    public void publish(LogRecord record) {
        if (frame == null) {
            frame = new LogFrame();
        }
        if (!frame.isVisible()) {
            frame.setVisible(true);
        }
        if (isLoggable(record)) {
            String message = getFormatter().format(record);
            frame.showInfo(message);
        }
    }

    @Override
    public void flush() {
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void close() {
        frame = null;
    }

}
