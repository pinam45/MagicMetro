/*
 * MIT License
 *
 * Copyright (c) 2016 TiWinDeTea - contact@tiwindetea.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.tiwindetea.magicmetro.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.arakhne.afc.math.geometry.d2.d.Point2d;
import org.tiwindetea.magicmetro.model.StationType;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * A JavaFx implementation of StationView.
 *
 * @author Maxime PINARD
 * @since 0.1
 */
public class ConcreteStationView extends Parent implements StationView {

	private static final int PASSENGER_COLUMNS = 6;
	private static final int PASSENGER_OFFSET_X = 10;
	private static final int PASSENGER_OFFSET_Y = 0;

	private final Shape sprite;
	private final List<PassengerView> passengers = new LinkedList<>();
	private final PassengerViewFactory passengerViewFactory;
	private final TilePane tilePane = new TilePane();
	private final ProgressIndicator progressIndicator = new ProgressIndicator();

	private Timeline timeline;

	public ConcreteStationView(Shape sprite,
	                           int spriteWidth,
	                           int spriteHeight,
	                           PassengerViewFactory passengerViewFactory) {
		this.sprite = sprite;
		this.getChildren().add(sprite);
		this.sprite.setTranslateX(0);
		this.sprite.setTranslateY(0);
		this.setLayoutX(-spriteWidth / 2);
		this.setLayoutY(-spriteHeight / 2);
		this.passengerViewFactory = passengerViewFactory;

		this.getChildren().add(this.tilePane);
		this.tilePane.setPrefColumns(PASSENGER_COLUMNS);
		this.tilePane.setTranslateX(spriteWidth + PASSENGER_OFFSET_X);
		this.tilePane.setTranslateY(PASSENGER_OFFSET_Y);

		this.progressIndicator.setMinSize(2 * spriteWidth, 2 * spriteHeight);
		this.progressIndicator.setTranslateX(-spriteWidth / 2);
		this.progressIndicator.setTranslateY(-spriteHeight / 2);
		this.progressIndicator.setStyle(" -fx-progress-color: darkgray;");
		this.progressIndicator.setProgress(0.7);
		this.progressIndicator.setVisible(false);
		this.getChildren().add(this.progressIndicator);
		this.progressIndicator.toBack();
	}

	@Override
	public void setPosition(@Nonnull Point2d position) {
		this.setTranslateX(position.getX());
		this.setTranslateY(position.getY());
	}

	@Override
	public void addPassenger(@Nonnull StationType wantedStation) {
		PassengerView passenger = new PassengerView(wantedStation,
		  this.passengerViewFactory.newPassengerView(wantedStation));
		this.passengers.add(passenger);
		Platform.runLater(() -> this.tilePane.getChildren().add(passenger));
	}

	@Override
	public void removePassenger(@Nonnull StationType wantedStation) {
		for(PassengerView passenger : this.passengers) {
			if(passenger.getWantedStation() == wantedStation) {
				this.passengers.remove(passenger);
				Platform.runLater(() -> this.tilePane.getChildren().remove(passenger));
				break;
			}
		}
	}

	@Override
	public void makeBigger() {
		//TODO
	}

	@Override
	public void warn(int seconds) {
		//TODO: speed depend on TimeManager
		Platform.runLater(() -> {
			this.progressIndicator.setProgress(0);
			this.progressIndicator.setVisible(true);
			this.timeline = new Timeline();
			this.timeline.setCycleCount(1);
			this.timeline.setAutoReverse(false);
			this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(seconds * 1000),
			  new KeyValue(this.progressIndicator.progressProperty(), 1)));
			this.timeline.play();
		});
	}

	@Override
	public void unWard() {
		this.progressIndicator.setVisible(false);
		this.timeline.stop();
	}

}
