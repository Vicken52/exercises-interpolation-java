/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appliedmed.exercises;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author zmichaels
 */
public class Prediction {

    private static class Point {

        final double x;
        final double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Interpolates between the 2D points stored in the list
     *
     * @param points list of points
     * @return interpolated list of points
     */
    private static List<Point> interpolate(List<Point> points) {
        /*
         * TODO: implement this.
         * 40% of the List's points were deleted (set to null). Interpolate 
         * those points. You may either do this in-place or calculate a new List.
         */
        for (int i = 1; i < points.size() - 1; i++) {
            if(points.get(i) == null && points.get(i-1) != null)
            {
                Point tempN = points.get(i-1);
                int count = 2;
                Point temp = null;

                while(temp == null && i+count < 199)
                {
                    temp = points.get(i+count);
                    count++;
                }

                if(temp != null)
                {
                    double dx = tempN.x - temp.x;
                    double dy = tempN.y - temp.y;

//                // Testing Changes
//                System.out.println("Dx = " + dx);
//                System.out.println("Dy = " + dy);
//                System.out.println("count = " + count);

                    for(int j = 1; j < count; j++)
                    {
                        points.set(i+j-1, new Point(tempN.x - (((double) j * dx) / (double) count),
                                tempN.y - (((double) j * dy / (double) count))));
                    }
                }
            }
        }

        return points;
    }

    public static void main(String[] args) throws Exception {
        final Path pExpected = Paths.get("expected.csv");
        final List<Point> expected;

        // load the expected values
        try (BufferedReader in = Files.newBufferedReader(pExpected)) {
            expected = in.lines()
                    .skip(1)
                    .map(str -> str.split(","))
                    .map(p -> new Point(Double.parseDouble(p[0]), Double.parseDouble(p[1])))
                    .collect(Collectors.toList());
        }

        // delete 40% of the values
        final List<Point> actual = new ArrayList<>(expected);

        for (int i = 0; i < actual.size(); i++) {
            if (Math.random() < 0.4) {
                actual.set(i, null);
            }
        }

        // create the interpolated list 
        final List<Point> interpolated = interpolate(actual);

//        // Testing changes
//        for(int i = 0; i < interpolated.size(); i++)
//        {
//            if(interpolated.get(i) != null)
//            {
//                System.out.println(interpolated.get(i).x + " " + interpolated.get(i).y);
//            }
//        }

        final JFrame window = new JFrame("Interpolation");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(640, 480);
        window.setVisible(true);
        window.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                final int width = this.getWidth();
                final int height = this.getHeight();

                g.clearRect(0, 0, width, height);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);

                final BufferedImage surface = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

                // draw the expected values as cyan
                for (Point p : expected) {
                    final int x = (int) p.x;
                    final int y = (int) p.y;

                    surface.setRGB(x, y, 0xFF00FFFF);
                }

                // draw the interpolated values as yellow
                for (Point p : interpolated) {
                    if (p != null) {
                        final int x = (int) p.x;
                        final int y = (int) p.y;

                        surface.setRGB(x, y, 0xFFFFFF00);
                    }
                }

                g.drawImage(surface, 0, 0, width, height, 0, 0, 256, 256, null);
            }
        });
    }
}
