package foo2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class SideScroll extends JLayeredPane {
    public static final String BG_IMG_PATH = "https://upload.wikimedia.org/wikipedia/commons"
            + "/a/ad/Tomada_da_cidade_de_S%C3%A3o_Salvador_s%C3%A9culo_XVIII_%28panor%C3%A2mico%29.jpg";
    public static final String CAMEL_PATH = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/PEO-bactrian_camel.svg/200px-PEO-bactrian_camel.svg.png";
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    protected static final int SCALE = 10;
    private JLabel backgroundLabel = new JLabel();
    JScrollPane scrollPane = new JScrollPane(backgroundLabel);
    private JLabel camelLabel = new JLabel();

    public SideScroll(Icon bgIcon, Icon camelIcon) {
        camelLabel.setIcon(camelIcon);
        camelLabel.setSize(camelLabel.getPreferredSize());

        JPanel camelPanel = new JPanel(new GridBagLayout());
        camelPanel.setOpaque(false);
        camelPanel.add(camelLabel);
        camelPanel.setSize(getPreferredSize());

        backgroundLabel.setIcon(bgIcon);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setSize(getPreferredSize());

        add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        add(camelPanel, JLayeredPane.PALETTE_LAYER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    moveImg(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    moveImg(1, 0);
                    break;
                case KeyEvent.VK_UP:
                    moveImg(0, -1);
                    break;
                case KeyEvent.VK_DOWN:
                    moveImg(0, 1);
                    break;
                default:
                    break;
                }
            }

            private void moveImg(int right, int down) {
                Rectangle rect = backgroundLabel.getVisibleRect();
                int x = rect.x + SCALE * right;
                int y = rect.y + SCALE * down;
                int width = rect.width;
                int height = rect.height;
                rect = new Rectangle(x, y, width, height);
                backgroundLabel.scrollRectToVisible(rect);
            }
        });

    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        return new Dimension(PREF_W, PREF_H);
    }

    private static void createAndShowGui() {
        Icon bgIcon = null;
        BufferedImage camel = null;
        Icon camelIcon = null;
        BufferedImage bgImg;
        try {
            URL bgImageUrl = new URL(BG_IMG_PATH);
            URL camelUrl = new URL(CAMEL_PATH);
            bgImg = ImageIO.read(bgImageUrl);
            camel = ImageIO.read(camelUrl);

            // make background one quarter the size because it's too big
            int imgW = bgImg.getWidth() / 4;
            int imgH = bgImg.getHeight() / 4;
            BufferedImage bgImage2 = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bgImage2.createGraphics();
            g2.drawImage(bgImg, 0, 0, imgW, imgH, null);
            g2.dispose();
            bgIcon = new ImageIcon(bgImage2);
            
            // flip camel image so facing right
            imgW = camel.getWidth();
            imgH = camel.getHeight();
            BufferedImage camelImg = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            g2 = camelImg.createGraphics();
            AffineTransform xform = AffineTransform.getTranslateInstance(imgW, 0);
            xform.scale(-1, 1);
            g2.drawImage(camel, xform, null);
            g2.dispose();
            camelIcon = new ImageIcon(camelImg);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        JFrame frame = new JFrame("SideScroll");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SideScroll(bgIcon, camelIcon));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }
}
