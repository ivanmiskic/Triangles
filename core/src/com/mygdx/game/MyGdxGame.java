package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class MyGdxGame implements ApplicationListener, InputProcessor
{
    int amount = 70, edge = 100, maxDist = 70, counter = 0, touchCounter = 0, numCombi = 8;
    static int width = 480, height = 800;
    //For rainbow generator
    float red = 255f, green = 0f, blue = 0f, growth = 3f;

    Ball[] balls;
    ArrayList<Triangle> triangles;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    //Color themes
    static Color Peach = new Color(1, 0.894f,0.667f, 1);
    static Color Black = new Color(0, 0, 0, 1);
    static Color Dark_gray= new Color(0.15f, 0.15f, 0.15f, 1);
    static Color Yellow = new Color(1, 1, 0.199f, 0.15f);
    static Color Green = new Color(0, 1, 0, 0.15f);
    static Color Gray = new Color(0.467f, 0.467f, 0.467f, 0.15f);
    static Color Purple = new Color(0.404f, 0.055f, 0.376f, 0.15f);
    static Color Light_blue = new Color(0, 1, 1, 0.15f);

    Color rand = new Color();
    Color shape = new Color();
    Color bg = Black;

    @Override
	public void create()
    {
        balls = new Ball[amount];
        camera = new OrthographicCamera(width,height);
        camera.position.set(width/2,height/2,0);
        camera.update();
        shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        createBalls(amount);
	}
	@Override
	public void render()
    {
//        System.out.println("fps: "+Gdx.graphics.getFramesPerSecond());
		Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);

        triangles = new ArrayList<Triangle>();
        // temporary balls used to figure out whether two balls are neighbours
        Ball b1, b2;
        for (int i=0; i < amount; i++)
        {
            balls[i].update();
            b1 = balls[i];
            b1.neighbours = new ArrayList<Ball>();
            b1.neighbours.add(b1);
            for (int j=i+1; j < amount; j++)
            {
                // 'i' to avoid having doubles, and '+1' to avoid comparing a ball to itself (I think...)
                b2=balls[j];
                float d = Vector2.dst( b1.loc.x,  b1.loc.y ,b2.loc.x , b2.loc.y); // comparing the location of both balls
                if (d>0 && d<maxDist)
                {
                    // if b2 is in the range then add it to the list of neighbours
                    b1.neighbours.add(b2);
                }
            }
            if (b1.neighbours.size()>1)
            {
                // if there are at least two neighbours then add the triangle(s) the the triangles array
                addTriangles(b1.neighbours);
            }
        }
        drawShapes();
        isRand();
	}
    @Override
    public void resize(int width, int height){}
    @Override
    public void pause(){}
    @Override
    public void resume(){}
    @Override
    public void dispose()
    {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        shapeRenderer.dispose();
        balls = null;
        triangles.clear();
    }

    class Ball
    {
        ArrayList<Ball> neighbours;  // arraylist of the 'ball' itself and all the others balls whose distance < maxDist to it
        float theta, radius = randomRange(20, 60);
        float offSet = randomRange(2, 6);
        int dir;
        Vector2 org = new Vector2(randomRange(edge, width-edge), randomRange(edge, height-edge));
        Vector2 loc = new Vector2(org.x+radius, org.y);

        Ball()
        {
            double r = Math.random();// clockwise or anti-clockwise
            if(r > 0.5)
                dir =-1;
            else
                dir = 1;
        }
        void update()
        {
//            float scal = map(sin(theta),-1,1,.5,2)
            float scal = 1;
            loc.x = org.x + (float) Math.sin(theta+offSet)*radius*scal;
            loc.y = org.y + (float) Math.cos(theta+offSet)*radius*scal;
            theta += (0.0523/2*dir);
        }
        void draw()
        {
            shapeRenderer.setColor(shape);
            shapeRenderer.circle(loc.x, loc.y , 5);
        }
        float randomRange(int min, int max)
        {
            int range = (max - min) + 1;
            float scaled = (float)(Math.random() * range);
            return scaled + min;
        }
    }
    class Triangle
    {
        Vector2 A, B, C;

        Triangle(Vector2 p1, Vector2 p2, Vector2 p3)
        {
            A = p1;
            B = p2;
            C = p3;
        }
        public void draw()
        {
            shapeRenderer.setColor(shape);
            shapeRenderer.triangle(A.x, A.y, B.x, B.y, C.x, C.y);
        }
    }

    void isRand()
    {
        //Rainbow generator
        if(shape.equals(rand))
        {
            if (red == 255 && green == 0)
                blue += growth;
            if (blue == 255 && green == 0)
                red -= growth;
            if (red == 0 && blue == 255)
                green += growth;
            if (green == 255 && red == 0)
                blue -= growth;
            if (blue == 0 && green == 255)
                red += growth;
            if (red == 255 && blue == 0)
                green -= growth;
            rand = new Color(red/255, green/255, blue/255, 0.15f);
            shape = rand;
        }
    }
    void createBalls(int amount)
    {
        // creating the location of the rotating reference points
        for (int i=0; i < amount; i++)
        {
            balls[i]= new Ball();
        }
    }
    void addTriangles(ArrayList<Ball> b_neighboors)
    {
        int s = b_neighboors.size();
        if (s > 2)
        {
            for (int i = 1; i < s-1; i ++)
            {
                for (int j = i+1; j < s; j ++)
                {
                    triangles.add(new Triangle(b_neighboors.get(0).loc, b_neighboors.get(i).loc, b_neighboors.get(j).loc));
                }
            }
        }
    }
    void drawShapes()
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawTriangles();
        drawBalls();
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawTriangles();
        drawBalls();
        shapeRenderer.end();
    }
    void drawTriangles()
    {
        for (Triangle t : triangles)
        {
            t.draw();
        }
    }
    void drawBalls()
    {
        for (Ball b : balls)
        {
            b.draw();
        }
    }

    //==============================================================================================
    //                                              INPUT
    //==============================================================================================
    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }
    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }
    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        touchCounter++;
        if(touchCounter > numCombi)
            touchCounter = 0;

        switch(touchCounter)
        {
            case 0:
                bg = Black;
                shape = rand;
                break;
            case 1:
                bg = Dark_gray;
                shape = rand;
                break;
            case 2:
                bg = Black;
                shape = Gray;
                break;
            case 3:
                bg = Black;
                shape = Green;
                break;
            case 4:
                bg = Black;
                shape = Yellow;
                break;
            case 5:
                bg = Black;
                shape = Purple;
                break;
            case 6:
                bg = Black;
                shape = Light_blue;
                break;
            case 7:
                bg = Dark_gray;
                shape = Green;
                break;
            case 8:
                bg = Peach;
                shape = Purple;
                break;
            default:
                break;
        }
        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return true;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        dispose();
        create();
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }
    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}