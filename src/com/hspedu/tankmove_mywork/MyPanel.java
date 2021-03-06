package com.hspedu.tankmove_mywork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

/**
 * @author 张宇
 * @version 1.0
 * 坦克大战的绘图区域
 */


public class MyPanel extends JPanel implements KeyListener, Runnable {
    //定义我的坦克
    Hero hero = null;

    //定义敌人的坦克，放入到Vector
    Vector<EnemyTank> enemyTanks = new Vector<>();
    int enemyTankSize = 3;

    //定义一个Vector，用于存放炸弹
    //说明，当子弹击中坦克时，就放入一个Bomb对象到bombs中
    Vector<Bomb> bombs = new Vector<>();

    //定义三张炸弹图片，用于显示爆炸效果
    Image image1 = null;
    Image image2 = null;
    Image image3 = null;

    //用于敌方坦克改变方向时间计数
    int count = 30;



    public MyPanel() {
        hero = new Hero(200, 200);
        hero.setSpeed(5);

        //初始化敌人坦克
        for (int i = 0; i < enemyTankSize; i++) {
            EnemyTank enemyTank = new EnemyTank((100 * (i + 1)), 0);
            enemyTank.setDirect(2);
            Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirect());
            enemyTank.shots.add(shot);
            new Thread(shot).start();
            enemyTanks.add(enemyTank);
        }

        //初始化图片对象,用于显示爆炸效果
        image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb.gif"));
        image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb.gif"));
        image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb.gif"));
    }

    //什么时候判断，我方的子弹是否击中了敌方的坦克
    public void hitTank(Shot s, EnemyTank enemyTank) {
        switch (enemyTank.getDirect()) {
            case 0: //坦克向上
            case 2: //坦克向下
                if (enemyTank.isLive && s.x > enemyTank.getX() && s.x < enemyTank.getX() + 40
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 60) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    //创建一个Bomb对象，加入到bombs集合
                    Bomb bomb = new Bomb(enemyTank.getX(), enemyTank.getY());
                    bombs.add(bomb);
                }
                break;
            case 1: //坦克向右
            case 3: //坦克向左
                if (enemyTank.isLive && s.x > enemyTank.getX() && s.x < enemyTank.getX() + 60
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 40) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    //创建一个Bomb对象，加入到bombs集合
                    Bomb bomb = new Bomb(enemyTank.getX(), enemyTank.getY());
                    bombs.add(bomb);
                }
                break;

        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.fillRect(0, 0, 1000, 750);//填充矩形，默认黑色

        //画出自己的坦克-封装方法
        drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 1);

        //画出hero射击的子弹
        if (hero.shot != null && hero.shot.isLive == true) {
            g.draw3DRect(hero.shot.x, hero.shot.y, 1, 1, false);
        }

        //如果bombs集合中有对象，就画出
        for(int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            if(bomb.life > 6) {
                g.drawImage(image1, bomb.x, bomb.y, 60, 60, this);
            } else if (bomb.life > 3) {
                g.drawImage(image2, bomb.x, bomb.y, 60, 60, this);
            } else {
                g.drawImage(image3, bomb.x, bomb.y, 60, 60, this);
            }
            bomb.lifeDown();
            //如果bomb life 为0， 就从bombs 的集合中删除
            if(bomb.life == 0) {
                bombs.remove(bomb);
            }
        }
        //画出敌人的坦克，遍历vector
        for (int i = 0; i < enemyTanks.size(); i++) {
            EnemyTank enemyTank = enemyTanks.get(i);
            if(enemyTank.isLive == true) {//当敌人的坦克是存活的，才画出该坦克
                drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirect(), 0);
                //画出 enemyTank 所有子弹
                for (int j = 0; j < enemyTank.shots.size(); j++) {
                    //取出子弹
                    Shot shot = enemyTank.shots.get(j);
                    //绘制
                    if (shot.isLive) {
                        g.draw3DRect(shot.x, shot.y, 1, 1, false);
                    } else {
                        //从Vector 移除
                        enemyTank.shots.remove(j);
                    }
                }
            }
        }

    }

    /**
     * @param x      坦克的左上角x坐标
     * @param y      坦克的左上角y坐标
     * @param g      画笔
     * @param direct 坦克方向（上下左右）
     * @param type   坦克类型
     */
    //编写方法，画出坦克
    public void drawTank(int x, int y, Graphics g, int direct, int type) {


        //根据不同类型的坦克，设置不同的颜色
        switch (type) {
            case 0: //敌人的坦克
                g.setColor(Color.cyan);
                break;
            case 1: //自己的坦克
                g.setColor(Color.yellow);
                break;
        }

        //根据坦克的方向，来绘制对应形状的坦克
        // direct 表示方向（0：向上 1：向右 2：向下 3 向左
        switch (direct) {
            case 0: //表示向上
                g.fill3DRect(x, y, 10, 60, false);//画出坦克左边的轮子
                g.fill3DRect(x + 30, y, 10, 60, false);//画出坦克右边的轮子
                g.fill3DRect(x + 10, y + 10, 20, 40, false);//画出坦克盖子
                g.fillOval(x + 10, y + 20, 20, 20);//画出圆形的盖子
                g.drawLine(x + 20, y + 30, x + 20, y); //画出坦克的炮管
                break;
            case 1: //表示向右
                g.fill3DRect(x, y, 60, 10, false);//画出坦克上面的轮子
                g.fill3DRect(x, y + 30, 60, 10, false);//画出坦克下面的轮子
                g.fill3DRect(x + 10, y + 10, 40, 20, false);//画出坦克盖子
                g.fillOval(x + 20, y + 10, 20, 20);//画出圆形的盖子
                g.drawLine(x + 30, y + 20, x + 60, y + 20); //画出坦克的炮管
                break;
            case 2: //表示向下
                g.fill3DRect(x, y, 10, 60, false);//画出坦克左边的轮子
                g.fill3DRect(x + 30, y, 10, 60, false);//画出坦克右边的轮子
                g.fill3DRect(x + 10, y + 10, 20, 40, false);//画出坦克盖子
                g.fillOval(x + 10, y + 20, 20, 20);//画出圆形的盖子
                g.drawLine(x + 20, y + 30, x + 20, y + 60); //画出坦克的炮管
                break;
            case 3: //表示向左
                g.fill3DRect(x, y, 60, 10, false);//画出坦克上面的轮子
                g.fill3DRect(x, y + 30, 60, 10, false);//画出坦克下面的轮子
                g.fill3DRect(x + 10, y + 10, 40, 20, false);//画出坦克盖子
                g.fillOval(x + 20, y + 10, 20, 20);//画出圆形的盖子
                g.drawLine(x + 30, y + 20, x, y + 20); //画出坦克的炮管
                break;
            default:
                System.out.println("暂时没有处理");
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    //处理wdsa 键按下的情况
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {//按下W键
            //改变坦克的方向
            hero.setDirect(0);
            //修改坦克的坐标 y -= 1
            hero.moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            hero.setDirect(1);
            hero.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            hero.setDirect(2);
            hero.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            hero.setDirect(3);
            hero.moveLeft();
        }

        //如果用户按下的是J，就发射
        if (e.getKeyCode() == KeyEvent.VK_J) {
            hero.shotEnemyTank();
            System.out.println("子弹被绘制");
        }
        //让面板重新绘制
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() { //每隔 100毫秒，重绘区域
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //判断是否击中了敌人坦克
            if(hero.shot != null && hero.shot.isLive) {
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);
                    hitTank(hero.shot, enemyTank);
                    if(!enemyTank.isLive) {
                        enemyTanks.remove(enemyTank);
                    }
                }
            }

            //敌方坦克随机移动
            //1. 敌方坦克会朝前走,也就是前进方向加
            //2. 每隔3s会改变方向，3s也就是上面刷新30次，使用一个count方法计数
            for(int i = 0; i < enemyTanks.size(); i++) {
                EnemyTank enemyTank = enemyTanks.get(i);
                //坦克前进
                enemyTank.moveOn();
                //坦克改变方向
                if(--count < 0) {
                    enemyTank.setDirect((int)(Math.random() * 4));
                    count = 30;
                }
            }

            this.repaint();
        }
    }
}
