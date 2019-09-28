package com.game.puzzle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainGame implements ApplicationListener {

    private SpriteBatch sbGame;
    private int[][] iComplette = {
            {4, 4, 1, 5, 5},
            {4, 4, 0, 5, 5},
            {1, 0, 1, 0, 1},
            {2, 2, 0, 3, 3},
            {2, 2, 1, 3, 3}
    };

    private int[][] iClean = {{0, 0, 1, 0, 0}, {0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1}, {0, 0, 0, 0, 0}, {0, 0, 1, 0, 0}};

    private int[][][] iLevels = {
            {
                    {4, 4, 1, 5, 5},
                    {4, 4, 0, 0, 5},
                    {1, 5, 1, 0, 1},
                    {2, 2, 0, 3, 3},
                    {2, 2, 1, 3, 3}
            }, {
            {5, 4, 1, 5, 3},
            {4, 4, 0, 5, 5},
            {1, 0, 1, 0, 1},
            {2, 2, 0, 3, 3},
            {4, 2, 1, 3, 2}
    }, {
            {5, 4, 1, 4, 5},
            {4, 5, 0, 5, 4},
            {1, 0, 1, 0, 1},
            {3, 2, 0, 2, 3},
            {2, 3, 1, 3, 2}
    }, {
            {3, 3, 1, 5, 5},
            {3, 3, 0, 5, 5},
            {1, 0, 1, 0, 1},
            {2, 2, 0, 4, 4},
            {2, 2, 1, 4, 4}
    }, {
            {2, 2, 1, 4, 4},
            {2, 2, 0, 4, 4},
            {1, 0, 1, 0, 1},
            {3, 3, 0, 5, 5},
            {3, 3, 1, 5, 5}
    }, {
            {2, 4, 1, 5, 4},
            {4, 3, 0, 2, 5},
            {1, 0, 1, 0, 1},
            {2, 5, 0, 4, 3},
            {3, 2, 1, 3, 5}
    }, {
            {2, 3, 1, 2, 3},
            {4, 5, 0, 4, 5},
            {1, 0, 1, 0, 1},
            {2, 3, 0, 2, 3},
            {4, 5, 1, 4, 5}
    }, {
            {0, 4, 1, 5, 0},
            {4, 4, 3, 5, 5},
            {1, 5, 1, 2, 1},
            {2, 2, 4, 3, 3},
            {0, 2, 1, 3, 0}
    }, {
            {2, 2, 1, 3, 3},
            {3, 3, 0, 2, 2},
            {1, 0, 1, 0, 1},
            {4, 4, 0, 5, 5},
            {5, 5, 1, 4, 4}
    }, {
            {2, 3, 1, 4, 5},
            {2, 3, 0, 4, 5},
            {1, 0, 1, 0, 1},
            {2, 3, 0, 4, 5},
            {2, 3, 1, 4, 5}
    }
    };

    private int iLevel;
    private int[] iLevelTop;
    private int iTop;
    private int[][] iMap;
    private boolean bDrag = false;

    private BitmapFont bfMainFont;
    private BitmapFont bfTimeFont;

    private Chip chMain;
    private Texture tBackGround;
    private Texture tChips;
    private TextureRegion[] trChips;
    private String sTime;
    private float fTime;
    private Music mScratch;
    private int iPage = 0;
    private boolean bSwitch;
    private Texture tLevels;
    private Texture tLevelsv;
    private IActivityRequestHandler myRequestHandler;
    private boolean bAds = false;
    private int iWidth;
    private boolean bVertical;
    private int iIteration;

    public MainGame(IActivityRequestHandler handler) {
        myRequestHandler = handler;
    }

    @Override
    public void create() {

        // Get all keys in one hand
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        sbGame = new SpriteBatch();
        chMain = new Chip();
        iMap = new int[5][5];

        loadState();

        try {
            mScratch = Gdx.audio.newMusic(Gdx.files.internal("scratch.ogg"));
        } catch (Exception e) {
            mScratch = null;
        }

        bfMainFont = new BitmapFont(Gdx.files.internal("main_font.fnt"),
                false);
        bfTimeFont = new BitmapFont(Gdx.files.internal("time_font.fnt"),
                false);

        tBackGround = new Texture(Gdx.files.internal("bg.png"));
        tLevels = new Texture(Gdx.files.internal("levels.png"));
        tLevelsv = new Texture(Gdx.files.internal("levelsv.png"));

        tChips = new Texture(Gdx.files.internal("buttons.png"));
        TextureRegion[][] trTmp = TextureRegion.split(tChips, 56, 56);
        trChips = new TextureRegion[4];
        System.arraycopy(trTmp[0], 0, trChips, 0, 4);
    }

    private void loadState() {
        String[] sMap = new String[5];
        Preferences prefs = Gdx.app.getPreferences("settings");
        iLevelTop = new int[10];

        fTime = prefs.getFloat("timer", 0);
        iLevel = prefs.getInteger("level", 0);

        String sTmp = prefs.getString("top", "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1");
        String[] sTopTemp = sTmp.split(",");
        for (int i = 0; i < sTopTemp.length; i++) {
            iLevelTop[i] = Integer.parseInt(sTopTemp[i]);
        }
        sMap[0] = prefs.getString("map_0", "");
        sMap[1] = prefs.getString("map_1", "");
        sMap[2] = prefs.getString("map_2", "");
        sMap[3] = prefs.getString("map_3", "");
        sMap[4] = prefs.getString("map_4", "");

        if (sMap[0].length() == 5) {
            for (int i = 0; i < 5; i++) {
                for (int t = 0; t < 5; t++) {
                    iMap[i][t] = Integer
                            .parseInt(sMap[i].substring(t, t + 1));
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                System.arraycopy(iLevels[iLevel][i], 0, iMap[i], 0, 5);
            }
        }
    }

    private void saveState() {
        String[] sMap = new String[5];
        Preferences prefs = Gdx.app.getPreferences("settings");

        prefs.putFloat("timer", fTime);
        prefs.putInteger("level", iLevel);

        for (int i = 0; i < 5; i++) {
            sMap[i] = "";
            for (int t = 0; t < 5; t++) {
                sMap[i] += "" + iMap[i][t];
            }
        }

        prefs.putString("map_0", sMap[0]);
        prefs.putString("map_1", sMap[1]);
        prefs.putString("map_2", sMap[2]);
        prefs.putString("map_3", sMap[3]);
        prefs.putString("map_4", sMap[4]);

        String sTmp = "";
        for (int anILevelTop : iLevelTop) {
            sTmp += anILevelTop + ",";
        }
        prefs.putString("top", sTmp);

        prefs.flush();
    }

    @Override
    public void resize(int width, int height) {
        bVertical = height > width;
        iWidth = width;
        iTop = height;
    }

    @Override
    public void render() {
        switch (iPage) {
            case 0:
                checkMove();
                renderPicture();
                break;
            case 1:
                renderComplette();
                break;
            case 2:
                renderLevel();
                break;
        }
    }

    private void renderLevel() {
        if (bAds) {
            bAds = false;
            if (myRequestHandler != null) {
                myRequestHandler.showAds(false);
            }
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sbGame.begin();
        if (bVertical) {
            sbGame.draw(tLevelsv, 0, iTop - 512);
        } else {
            sbGame.draw(tLevels, 0, 320 - 512);
        }

        // Render best times
        if (bVertical) {
            for (int i = 0; i < 3; i++) {
                for (int t = 0; t < 3; t++) {
                    bfMainFont.drawWrapped(sbGame, "" + (i + (t * 3) + 1),
                            22 + i * 96,
                            iTop - 120 - t * 90, 80,
                            BitmapFont.HAlignment.CENTER);

                    int iTopTime = iLevelTop[t * 3 + i];
                    if (iTopTime > -1) {
                        if (iTopTime / 60 < 10) {
                            sTime = "0" + iTopTime / 60 + ":";
                        } else {
                            sTime = iTopTime / 60 + ":";
                        }

                        if (iTopTime % 60 < 10) {
                            sTime += "0" + iTopTime % 60;
                        } else {
                            sTime += (int) (iTopTime % 60);
                        }
                        bfTimeFont.drawWrapped(sbGame, sTime,
                                22 + i * 96,
                                iTop - 165 - t * 90, 80,
                                BitmapFont.HAlignment.CENTER);
                    }
                }
            }
            bfMainFont.drawWrapped(sbGame, "10",
                    22 + 96,
                    iTop - 120 - 3 * 90, 80,
                    BitmapFont.HAlignment.CENTER);

            int iTopTime = iLevelTop[9];
            if (iTopTime > -1) {
                if (iTopTime / 60 < 10) {
                    sTime = "0" + iTopTime / 60 + ":";
                } else {
                    sTime = iTopTime / 60 + ":";
                }

                if (iTopTime % 60 < 10) {
                    sTime += "0" + iTopTime % 60;
                } else {
                    sTime += (int) (iTopTime % 60);
                }
                bfTimeFont.drawWrapped(sbGame, sTime,
                        22 + 96,
                        iTop - 165 - 3 * 90, 80,
                        BitmapFont.HAlignment.CENTER);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                for (int t = 0; t < 2; t++) {
                    bfMainFont.drawWrapped(sbGame, "" + (i + (t * 5) + 1),
                            8 + i * 96, iTop - 103 - t * 96, 80,
                            BitmapFont.HAlignment.CENTER);

                    int iTopTime = iLevelTop[t * 5 + i];
                    if (iTopTime > -1) {
                        if (iTopTime / 60 < 10) {
                            sTime = "0" + iTopTime / 60 + ":";
                        } else {
                            sTime = iTopTime / 60 + ":";
                        }

                        if (iTopTime % 60 < 10) {
                            sTime += "0" + iTopTime % 60;
                        } else {
                            sTime += (int) (iTopTime % 60);
                        }
                        bfTimeFont.drawWrapped(sbGame, sTime, 8 + i * 96, iTop
                                - 148 - t * 96, 80, BitmapFont.HAlignment.CENTER);
                    }
                }
            }
        }

        sbGame.end();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            bSwitch = true;
            iPage = 0;
        }

        if (Gdx.input.isTouched()) {
            int iXpos = Gdx.input.getX();
            int iYpos = Gdx.input.getY();

            if (bVertical) {
                if (!bSwitch) {
                    bSwitch = true;
                    if (iYpos > 114 && iYpos < 193) {
                        if (iXpos > 20 && iXpos < 100) {
                            iLevel = 0;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 117 && iXpos < 195) {
                            iLevel = 1;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 213 && iXpos < 290) {
                            iLevel = 2;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iYpos > 204 && iYpos < 283) {
                        if (iXpos > 20 && iXpos < 100) {
                            iLevel = 3;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 117 && iXpos < 195) {
                            iLevel = 4;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 213 && iXpos < 290) {
                            iLevel = 5;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iYpos > 295 && iYpos < 373) {
                        if (iXpos > 20 && iXpos < 100) {
                            iLevel = 6;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 117 && iXpos < 195) {
                            iLevel = 7;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 213 && iXpos < 290) {
                            iLevel = 8;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iYpos > 385 && iYpos < 464) {
                        if (iXpos > 117 && iXpos < 195) {
                            iLevel = 9;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iLevel < 10) {
                        for (int i = 0; i < 5; i++) {
                            System.arraycopy(iLevels[iLevel][i], 0, iMap[i], 0, 5);
                        }
                    }
                }
            } else {
                if (!bSwitch) {
                    bSwitch = true;
                    if (iYpos > 97 && iYpos < 175) {
                        if (iXpos > 8 && iXpos < 87) {
                            iLevel = 0;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 104 && iXpos < 183) {
                            iLevel = 1;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 200 && iXpos < 279) {
                            iLevel = 2;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 296 && iXpos < 375) {
                            iLevel = 3;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 392 && iXpos < 471) {
                            iLevel = 4;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iYpos > 193 && iYpos < 272) {
                        if (iXpos > 8 && iXpos < 87) {
                            iLevel = 5;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 104 && iXpos < 183) {
                            iLevel = 6;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 200 && iXpos < 279) {
                            iLevel = 7;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 296 && iXpos < 375) {
                            iLevel = 8;
                            fTime = 0;
                            iPage = 0;
                        }
                        if (iXpos > 392 && iXpos < 471) {
                            iLevel = 9;
                            fTime = 0;
                            iPage = 0;
                        }
                    }
                    if (iLevel < 10) {
                        for (int i = 0; i < 5; i++) {
                            System.arraycopy(iLevels[iLevel][i], 0, iMap[i], 0, 5);
                        }
                    }
                }
            }
        } else {
            bSwitch = false;
        }
    }

    private void renderComplette() {
        if (!bAds) {
            if (myRequestHandler != null) {
                bAds = true;
                myRequestHandler.showAds(true);
            }
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sbGame.begin();

        bfMainFont.setColor(Color.WHITE);
        bfMainFont.drawWrapped(sbGame, "Level " + (iLevel + 1), 0,
                iTop - 100, iWidth, BitmapFont.HAlignment.CENTER);
        bfMainFont.drawWrapped(sbGame, "Time " + sTime, 0, iTop - 150, iWidth,
                BitmapFont.HAlignment.CENTER);
        sbGame.end();
        if (Gdx.input.isTouched()) {
            if (!bSwitch) {
                if (iLevel < 11
                        && (iLevelTop[iLevel] > fTime || iLevelTop[iLevel] == -1)) {
                    iLevelTop[iLevel] = (int) fTime;
                }

                iLevel++;
                fTime = 0;
                if (iLevel < 10) {
                    for (int i = 0; i < 5; i++) {
                        System.arraycopy(iLevels[iLevel][i], 0, iMap[i], 0, 5);
                    }
                } else {
                    iLevel++;
                    fTime = 0;

                    for (int i = 0; i < 5; i++) {
                        System.arraycopy(iClean[i], 0, iMap[i], 0, 5);
                    }
                    int i = 15;
                    do {
                        int iXpos = (int) (Math.random() * 5);
                        int iYpos = (int) (Math.random() * 5);
                        if (iMap[iYpos][iXpos] == 0) {
                            iMap[iYpos][iXpos] = 2 + (i / 4);
                            i--;
                        }
                    } while (i >= 0);
                }
                iPage = 0;
            }
            bSwitch = true;
        } else {
            bSwitch = false;
        }
    }

    private void checkReady() {
        boolean bEqual = true;
        for (int i = 0; i < 5; i++) {
            for (int t = 0; t < 5; t++) {
                if (iComplette[i][t] != iMap[i][t]) {
                    bEqual = false;
                }
            }
        }

        if (bEqual) {
            iPage = 1;
            bSwitch = true;
        }
    }

    private void renderPicture() {

        if (bVertical) {
            if (myRequestHandler != null) {
                bAds = true;
                myRequestHandler.showAds(true);
            }
        } else {
            if (myRequestHandler != null) {
                bAds = false;
                myRequestHandler.showAds(false);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sbGame.begin();

        // Render back
        if (bVertical && bAds) {
            sbGame.draw(tBackGround, 0, iTop - 512 - 50);
        } else {
            sbGame.draw(tBackGround, 0, iTop - 512);
        }

        // Render static chips
        for (int i = 0; i < 5; i++) {
            for (int t = 0; t < 5; t++) {
                if (iMap[i][t] > 1) {
                    if (bVertical && bAds) {
                        sbGame.draw(trChips[iMap[i][t] - 2], 20 + t * 56,
                                iTop - 320 + 20 + i * 56 - 50);
                    } else {
                        sbGame.draw(trChips[iMap[i][t] - 2], 20 + t * 56,
                                iTop - 320 + 20 + i * 56);
                    }
                }
            }
        }

        // Render active chip
        if (chMain.bDrag) {
            if (bVertical && bAds) {
                sbGame.draw(trChips[chMain.iType - 2], 20 + chMain.fXpos,
                        iTop - 320 + 20 + chMain.fYpos - 50);
            } else {
                sbGame.draw(trChips[chMain.iType - 2], 20 + chMain.fXpos,
                        iTop - 320 + 20 + chMain.fYpos);
            }
        }

        // Render info
        if (bVertical) {
            int iSub = 0;
            if (!bAds) {
                iSub = 50;
            }
            bfMainFont.setColor(Color.WHITE);
            bfMainFont.drawWrapped(sbGame, "Level", 0, iTop - 380 + iSub, 105,
                    BitmapFont.HAlignment.CENTER);
            if (iLevel < 11) {
                bfMainFont.drawWrapped(sbGame, "" + (iLevel + 1), 0,
                        iTop - 410 + iSub, 105, BitmapFont.HAlignment.CENTER);
                if (iLevelTop[iLevel] > -1) {
                    bfMainFont.drawWrapped(sbGame, "Best", 210, iTop - 380 + iSub,
                            105, BitmapFont.HAlignment.CENTER);
                    if (iLevelTop[iLevel] / 60 < 10) {
                        sTime = "0" + iLevelTop[iLevel] / 60 + ":";
                    } else {
                        sTime = iLevelTop[iLevel] / 60 + ":";
                    }

                    if (iLevelTop[iLevel] % 60 < 10) {
                        sTime += "0" + iLevelTop[iLevel] % 60;
                    } else {
                        sTime += (int) (iLevelTop[iLevel] % 60);
                    }
                    bfMainFont.drawWrapped(sbGame, sTime, 210, iTop - 410 + iSub,
                            105, BitmapFont.HAlignment.CENTER);
                }
            } else {
                bfMainFont.drawWrapped(sbGame, "Rand", 0, iTop - 410 + iSub,
                        105, BitmapFont.HAlignment.CENTER);
            }
            bfMainFont.drawWrapped(sbGame, "Time", 105, iTop - 380 + iSub, 105,
                    BitmapFont.HAlignment.CENTER);
            fTime += Gdx.graphics.getDeltaTime();

            if ((int) (fTime / 60) < 10) {
                sTime = "0" + (int) (fTime / 60) + ":";
            } else {
                sTime = (int) (fTime / 60) + ":";
            }

            if ((int) (fTime % 60) < 10) {
                sTime += "0" + (int) (fTime % 60);
            } else {
                sTime += (int) (fTime % 60);
            }
            bfMainFont.drawWrapped(sbGame, sTime, 105, iTop - 410 + iSub, 105,
                    BitmapFont.HAlignment.CENTER);
        } else {
            bfMainFont.setColor(Color.WHITE);
            bfMainFont.drawWrapped(sbGame, "Level", 320, iTop - 20, iWidth - 320,
                    BitmapFont.HAlignment.CENTER);
            if (iLevel < 11) {
                bfMainFont.drawWrapped(sbGame, "" + (iLevel + 1), 320,
                        iTop - 50, iWidth - 320, BitmapFont.HAlignment.CENTER);
                if (iLevelTop[iLevel] > -1) {
                    bfMainFont.drawWrapped(sbGame, "Best", 320, iTop - 180,
                            iWidth - 320, BitmapFont.HAlignment.CENTER);
                    if (iLevelTop[iLevel] / 60 < 10) {
                        sTime = "0" + iLevelTop[iLevel] / 60 + ":";
                    } else {
                        sTime = iLevelTop[iLevel] / 60 + ":";
                    }

                    if (iLevelTop[iLevel] % 60 < 10) {
                        sTime += "0" + iLevelTop[iLevel] % 60;
                    } else {
                        sTime += (int) (iLevelTop[iLevel] % 60);
                    }
                    bfMainFont.drawWrapped(sbGame, sTime, 320, iTop - 210,
                            iWidth - 320, BitmapFont.HAlignment.CENTER);
                }
            } else {
                bfMainFont.drawWrapped(sbGame, "Random", 320, iTop - 50,
                        iWidth - 320, BitmapFont.HAlignment.CENTER);
            }
            bfMainFont.drawWrapped(sbGame, "Time", 320, iTop - 100, iWidth - 320,
                    BitmapFont.HAlignment.CENTER);
            fTime += Gdx.graphics.getDeltaTime();

            if ((int) (fTime / 60) < 10) {
                sTime = "0" + (int) (fTime / 60) + ":";
            } else {
                sTime = (int) (fTime / 60) + ":";
            }

            if ((int) (fTime % 60) < 10) {
                sTime += "0" + (int) (fTime % 60);
            } else {
                sTime += (int) (fTime % 60);
            }
            bfMainFont.drawWrapped(sbGame, sTime, 320, iTop - 130, iWidth - 320,
                    BitmapFont.HAlignment.CENTER);
        }

        sbGame.end();
    }

    private void checkMove() {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) && !bSwitch) {
            Gdx.app.exit();
        }

        if (Gdx.input.isTouched()) {
            float fAlign = 1;
            float fPointX = (float) Gdx.input.getX() / fAlign - 20;
            float fPointY;
            if (bVertical) {
                if (bAds) {
                    fPointY = iTop - Gdx.input.getY() - 20 - 160;
                } else {
                    fPointY = iTop - Gdx.input.getY() - 20 - 210;
                }
            } else {
                fPointY = iTop - Gdx.input.getY() - 20;
            }
            if (bVertical) {
                if (!bDrag && fPointX > 0 && fPointX < 100
                        && fPointY < -33 && fPointY > -100) {
                    bSwitch = true;
                    iPage = 2;
                }
            } else {
                if (!bDrag && fPointX > 313 && fPointX < 442
                        && fPointY > 224 && fPointY < 280) {
                    bSwitch = true;
                    iPage = 2;
                }
            }

            int iTargetX = (int) fPointX / 56;
            int iTargetY = (int) fPointY / 56;

            if (!bDrag) {
                if (!bSwitch) {
                    if (iTargetY >= 0 && iTargetY < 5 && iTargetX >= 0
                            && iTargetX < 5 && iMap[iTargetY][iTargetX] > 1) {
                        chMain.bDrag = true;
                        chMain.iType = iMap[iTargetY][iTargetX];
                        chMain.fXpos = iTargetX * 56;
                        chMain.fYpos = iTargetY * 56;
                        chMain.fXdif = fPointX - iTargetX * 56;
                        chMain.fYdif = fPointY - iTargetY * 56;
                        iMap[iTargetY][iTargetX] = 0;
                        bDrag = true;
                    }
                }
            } else {
                float fVol = 0;
                do {
                    chMain.iXvel = 0;
                    chMain.iYvel = 0;
                    if (chMain.fXpos < 224
                            && chMain.fXpos + chMain.fXdif < fPointX
                            && iMap[(int) (chMain.fYpos) / 56][(int) (chMain.fXpos + 56) / 56] == 0
                            && iMap[(int) (chMain.fYpos + 55) / 56][(int) (chMain.fXpos + 56) / 56] == 0) {
                        chMain.iXvel = 1;
                    }

                    if (chMain.fXpos > 0
                            && chMain.fXpos + chMain.fXdif > fPointX
                            && iMap[(int) (chMain.fYpos) / 56][(int) (chMain.fXpos - 1) / 56] == 0
                            && iMap[(int) (chMain.fYpos + 55) / 56][(int) (chMain.fXpos - 1) / 56] == 0) {
                        chMain.iXvel = -1;
                    }

                    if (chMain.fYpos < 224
                            && chMain.fYpos + chMain.fYdif < fPointY
                            && iMap[(int) (chMain.fYpos + 56) / 56][(int) (chMain.fXpos) / 56] == 0
                            && iMap[(int) (chMain.fYpos + 56) / 56][(int) (chMain.fXpos + 55) / 56] == 0) {
                        chMain.iYvel = 1;
                    }

                    if (chMain.fYpos > 0
                            && chMain.fYpos + chMain.fYdif > fPointY
                            && iMap[(int) (chMain.fYpos - 1) / 56][(int) (chMain.fXpos) / 56] == 0
                            && iMap[(int) (chMain.fYpos - 1) / 56][(int) (chMain.fXpos + 55) / 56] == 0) {
                        chMain.iYvel = -1;
                    }

                    if (chMain.iXvel != 0 && chMain.iYvel != 0) {
                        float fXdiff = Math.abs(chMain.fXpos + chMain.fXdif
                                - fPointX);
                        float fYdiff = Math.abs(chMain.fYpos + chMain.fYdif
                                - fPointY);
                        if (fXdiff > fYdiff) {
                            chMain.iYvel = 0;
                        } else {
                            chMain.iXvel = 0;
                        }
                    }

                    if (chMain.iXvel != 0 || chMain.iYvel != 0) {
                        if (fVol < 0.2) {
                            fVol += 0.03f;
                        }
                        if (mScratch != null) {
                            iIteration = 5;
                            mScratch.setLooping(true);
                            if (!mScratch.isPlaying()) {
                                mScratch.play();
                            }
                            mScratch.setVolume(fVol);
                        }
                    } else {
                        if (fVol > 0) {
                            fVol -= 0.01f;
                        }
                        if (mScratch != null) {
                            if (iIteration <= 0) {
                                if (mScratch.isPlaying()) {
                                    mScratch.stop();
                                }
                            } else {
                                iIteration--;
                            }
                        }
                    }

                    if (chMain.iXvel != 0) {
                        chMain.fXpos += chMain.iXvel;
                    }

                    if (chMain.iYvel != 0) {
                        chMain.fYpos += chMain.iYvel;
                    }
                } while (chMain.iXvel != 0 || chMain.iYvel != 0);
            }
        } else {
            bDrag = false;
            bSwitch = false;
            iIteration = 0;
            if (mScratch.isPlaying()) {
                mScratch.stop();
            }
            if (chMain.bDrag) {
                chMain.bDrag = false;
                iMap[(int) (chMain.fYpos + 28) / 56][(int) (chMain.fXpos + 28) / 56] = chMain.iType;
                checkReady();
            }
        }
    }

    @Override
    public void pause() {
        saveState();

        bfMainFont.dispose();
        bfTimeFont.dispose();
        tBackGround.dispose();
        tChips.dispose();
        tLevels.dispose();
        tLevelsv.dispose();
    }

    @Override
    public void resume() {
        loadState();

        bfMainFont = new BitmapFont(Gdx.files.internal("main_font.fnt"),
                false);
        bfTimeFont = new BitmapFont(Gdx.files.internal("time_font.fnt"),
                false);

        tBackGround = new Texture(Gdx.files.internal("bg.png"));
        tChips = new Texture(Gdx.files.internal("buttons.png"));
        TextureRegion[][] trTmp = TextureRegion.split(tChips, 56, 56);
        trChips = new TextureRegion[4];
        System.arraycopy(trTmp[0], 0, trChips, 0, 4);

        tLevels = new Texture(Gdx.files.internal("levels.png"));
        tLevelsv = new Texture(Gdx.files.internal("levelsv.png"));
    }

    @Override
    public void dispose() {
        saveState();

        bfMainFont.dispose();
        bfTimeFont.dispose();
        tBackGround.dispose();
        tChips.dispose();
        tLevels.dispose();
        tLevelsv.dispose();
    }
}
