package com.example.dat.geneticalgorithm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.dat.geneticalgorithm.model.GraphicResult;
import com.example.dat.geneticalgorithm.model.ItemOfSubSpace;
import com.example.dat.geneticalgorithm.model.Population;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText editText_number_of_organisms, editText_number_of_generations, editText_number_of_reproductions,
            editText_mutate_chance, editText_strong_chance, editText_die_chance, editText_bit_string_length;
    Button buttonStart;
    ProgressBar progressBar;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIDs();
        setEvents();
    }

    private void getIDs() {
        editText_number_of_organisms = (EditText) findViewById(R.id.editText_number_of_organisms);
        editText_number_of_generations = (EditText) findViewById(R.id.editText_number_of_generations);
        editText_number_of_reproductions = (EditText) findViewById(R.id.editText_number_of_reproductions);
        editText_mutate_chance = (EditText) findViewById(R.id.editText_mutate_chance);
        editText_strong_chance = (EditText) findViewById(R.id.editText_strong_chance);
        editText_die_chance = (EditText) findViewById(R.id.editText_die_chance);
        editText_bit_string_length = (EditText) findViewById(R.id.editText_bit_string_length);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);
    }

    private void setEvents() {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.setVisibility(View.INVISIBLE);
                startProcess();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startProcess() {
        final int numberOfOrganism = Integer.valueOf(editText_number_of_organisms.getText() + ""),
                lengthOfBitString = Integer.valueOf(editText_bit_string_length.getText() + ""),
                numberOfReproductions = Integer.valueOf(editText_number_of_reproductions.getText() + ""),
                numberOfGenerations = Integer.valueOf(editText_number_of_generations.getText() + "");
        final double chanceMutate = Double.valueOf(editText_mutate_chance.getText() + ""),
                strongChance = Double.valueOf(editText_strong_chance.getText() + ""),
                dieChance = Double.valueOf(editText_die_chance.getText() + "");
        /*generateSpace(lengthOfBitString);
        generateSubSpace(lengthOfBitString);*/

        new AsyncTask<Void, Void, Population>() {

            @Override
            protected Population doInBackground(Void... params) {
                Population population = new Population(numberOfGenerations, numberOfOrganism, lengthOfBitString, numberOfReproductions, chanceMutate, strongChance, dieChance);
                population.generatePopulation();
        /*Log.d("Pop", population.toString());
        Log.d("Space", population.getSpace().toString());
        Log.d("SubSpace", population.getSubSpace().toString());*/
                population.startOperations();
                Log.d("Done", "Done");
                Log.d("Population", population.getListOfOrganisms().size() + "");
                return population;
            }

            @Override
            protected void onPostExecute(Population population) {
                progressBar.setVisibility(View.INVISIBLE);
                redraw(population.getGraphicResults());
            }
        }.execute();


    }

    private void redraw(ArrayList<GraphicResult> graphicResults) {
        graph.setVisibility(View.VISIBLE);
        graph.removeAllSeries();
        int n = graphicResults.size();
        DataPoint[] dataPoints = new DataPoint[n];
        for (int i = 0; i < graphicResults.size(); i++) {
            DataPoint dataPoint = new DataPoint(i, graphicResults.get(i).getAverage());
            dataPoints[i] = dataPoint;
        }
        LineGraphSeries<DataPoint> average = new LineGraphSeries<DataPoint>(
                dataPoints
        );
        average.setColor(getResources().getColor(R.color.blue));
        average.setTitle("Average");

        DataPoint[] dataPoints2 = new DataPoint[n];
        for (int i = 0; i < graphicResults.size(); i++) {
            DataPoint dataPoint = new DataPoint(i, graphicResults.get(i).getWeakest());
            dataPoints2[i] = dataPoint;
        }
        LineGraphSeries<DataPoint> weakest = new LineGraphSeries<DataPoint>(
                dataPoints2
        );
        weakest.setColor(getResources().getColor(R.color.red));
        weakest.setTitle("Weakest");

        DataPoint[] dataPoints3 = new DataPoint[n];
        for (int i = 0; i < graphicResults.size(); i++) {
            DataPoint dataPoint = new DataPoint(i, graphicResults.get(i).getStrongest());
            dataPoints3[i] = dataPoint;
        }
        LineGraphSeries<DataPoint> strongest = new LineGraphSeries<DataPoint>(
                dataPoints3
        );
        strongest.setColor(getResources().getColor(R.color.green));
        strongest.setTitle("Strongest");
        graph.addSeries(average);
        graph.addSeries(strongest);
        graph.addSeries(weakest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
