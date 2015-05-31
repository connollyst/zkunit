package org.zkoss.zkunit;

import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Rows;

/**
 * Utilities for use in ZK unit tests.
 *
 * @author Sean Connolly
 */
public class ZKUtils {

    /**
     * Create a ZK {@link Grid} component for testing with.<br/>
     * A grid created in a normal ZK environment comes with {@link Rows} and {@link Columns}, even if not explicitly
     * defined in ZUL markup. However, a grid created outside any ZK environment (such as in a unit test) does not have
     * any of these. Here we return a grid with columns and rows initialized.
     *
     * @return an initialized grid with no columns or rows
     */
    public static Grid getGrid() {
        return getGrid(0);
    }

    /**
     * Create a ZK {@link Grid} component for testing with.<br/>
     * A grid created in a normal ZK environment comes with {@link Rows} and {@link Columns}, even if not explicitly
     * defined in ZUL markup. However, a grid created outside any ZK environment (such as in a unit test) does not have
     * any of these. Here we return a grid with columns and rows initialized.
     *
     * @param columns the number of columns required
     * @return an initialized grid with no rows, but the given number of columns
     */
    public static Grid getGrid(int columns) {
        Grid grid = new Grid();
        Rows rows = new Rows();
        Columns cols = new Columns();
        for (int i = 0; i < columns; i++) {
            cols.appendChild(new Column());
        }
        grid.appendChild(rows);
        grid.appendChild(cols);
        return grid;
    }

}
